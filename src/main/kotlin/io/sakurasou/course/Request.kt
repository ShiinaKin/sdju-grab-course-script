package io.sakurasou.course

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.common.ApiResult
import io.sakurasou.commonRequestBuilder
import io.sakurasou.config
import io.sakurasou.entity.*
import io.sakurasou.ioScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.time.Duration

/**
 * @author ShiinaKin
 * 2024/7/8 15:23
 */
val okHttpClient = OkHttpClient().newBuilder().build()
val jsonMapper: ObjectMapper = JsonMapper().registerModules(kotlinModule(), JavaTimeModule())

val logger = KotlinLogging.logger { }

lateinit var majorPlan: MajorPlan
lateinit var allPlanCourse: List<PlanCourse>

fun getMajorPlan() {
    val majorPlanRequest = commonRequestBuilder
        .url("https://jwgl.sdju.edu.cn/course-selection-api/api/v1/student/course-select/major-plan/${config.selectTurnId}/${config.studentId}")
        .get()
        .build()
    okHttpClient.newCall(majorPlanRequest).execute().run {
        if (!isSuccessful) {
            logger.warn { "Request failed" }
            return
        }
        val result = jsonMapper.readValue<ApiResult<MajorPlan>>(body!!.string())
        majorPlan = result.data!!
        allPlanCourse = majorPlan.flat()
    }
}

fun getCourseDetail(courseId: Long): Map<Pair<Long, String>, String> {
    val courseFilter = CourseFilter(config.selectTurnId, config.studentId, courseId)
    val courseFilterJson = jsonMapper.writeValueAsString(courseFilter)
    val courseFilterRequestBody = courseFilterJson.toRequestBody(("application/json").toMediaType())

    val courseFilterRequest = commonRequestBuilder
        .url("https://jwgl.sdju.edu.cn/course-selection-api/api/v1/student/course-select/query-lesson/${config.studentId}/${config.selectTurnId}")
        .post(courseFilterRequestBody)
        .build()
    return okHttpClient.newCall(courseFilterRequest).execute().run {
        if (!isSuccessful) {
            logger.warn { "Request failed" }
            return emptyMap()
        }
        val result = jsonMapper.readValue<ApiResult<LessonQueryResult>>(body!!.string())
        val lessons = result.data?.lessons

        if (lessons.isNullOrEmpty()) {
            return emptyMap()
        }

        lessons.associate {
            it.id to "${it.course.nameZh}/${it.course.nameEn} ${it.dateTimePlace.text}" to "id: ${it.id} code: ${it.code} " +
                    "name: ${it.course.nameZh}/${it.course.nameEn} " +
                    "teacher: ${it.teachers.joinToString { t -> "${t.nameZh}/${t.nameEn}" }} " +
                    "${it.campus.nameZh}/${it.campus.nameEn} ${it.dateTimePlace.text}"
        }
    }
}

suspend fun grabCourses(retryTime: Int = 10, delay: String = "100ms") {
    val needGrabCourseMap = config.needGrabCourseMap
    needGrabCourseMap.forEach {
        val addRequest = AddRequest(
            config.studentId,
            config.selectTurnId,
            // 可以传多个，但atomic，所以只传一个
            listOf(RequestMiddleDto(it.key)),
            null
        )
        val job = ioScope.launch {
            try {
                for (i in 1..retryTime) {
                    val grabResult = grabCourse(config.studentId, addRequest)
                    if (grabResult) {
                        logger.info { "Grab course ${it.value} success" }
                        needGrabCourseMap.remove(it.key)
                        return@launch
                    }
                    logger.debug { "Grab course ${it.value} failed, retry * ${i - 1}" }
                    delay(Duration.parse(delay))
                }
                logger.warn { "Grab course ${it.value} failed.." }
            } catch (e: Exception) {
                logger.error(e) { "Unexpected Exception:" }
            }
        }
        // block main thread
        job.join()
    }
}

fun grabCourse(studentId: Long, addRequest: AddRequest): Boolean {
    val addRequestJson = jsonMapper.writeValueAsString(addRequest)
    val reqIdRequestBody = addRequestJson.toRequestBody(("application/json").toMediaType())
    val reqIdRequest = commonRequestBuilder
        .url("https://jwgl.sdju.edu.cn/course-selection-api/api/v1/student/course-select/add-request")
        .post(reqIdRequestBody)
        .build()

    val reqId = okHttpClient.newCall(reqIdRequest).execute().run {
        if (!isSuccessful) {
            logger.warn { "Request failed" }
            return false
        }
        val bodyStr = body?.string()
        logger.debug { "body: $bodyStr" }
        val result = jsonMapper.readValue<ApiResult<String>>(bodyStr!!)
        result.data!!
    }

    val grabCourseRequest = commonRequestBuilder
        .url("https://jwgl.sdju.edu.cn/course-selection-api/api/v1/student/course-select/add-drop-response/$studentId/$reqId")
        .get()
        .build()
    val grabResult = okHttpClient.newCall(grabCourseRequest).execute().run {
        if (!isSuccessful) {
            logger.warn { "Request failed" }
            return false
        }
        val bodyStr = body?.string()
        logger.debug { "body: $bodyStr" }
        val result = jsonMapper.readValue<ApiResult<GrabRequestResult>>(bodyStr!!)
        result.data
    }
    if (grabResult == null) {
        logger.debug { "GrabRequestResult is null, probably cause by too fast request" }
        return false
    }
    return if (grabResult.success) true else {
        logger.debug { grabResult.errorMessage?.text }
        false
    }
}