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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * @author ShiinaKin
 * 2024/7/8 15:23
 */
val okHttpClient = OkHttpClient().newBuilder().build()
val jsonMapper: ObjectMapper = JsonMapper().registerModules(kotlinModule(), JavaTimeModule())

val logger = KotlinLogging.logger { }

val classifiedLessons = mutableListOf<Triple<String, Long, List<Lesson>>>()

fun isSelectStart(): Boolean {
    val request = commonRequestBuilder
        .url("https://jwgl.sdju.edu.cn/course-selection-api/api/v1/student/course-select/students")
        .get()
        .build()
    return okHttpClient.newCall(request).execute().use {
        if (!it.isSuccessful) throw RuntimeException("getSelectStart Request failed, code: ${it.code}, msg: ${it.message}")
        val result = jsonMapper.readValue<ApiResult<List<Int>>>(it.body!!.string())
        result.data?.isNotEmpty() ?: false
    }
}

fun getOpenedTurn(): List<SelectTurn> {
    val request = commonRequestBuilder
        .url("https://jwgl.sdju.edu.cn/course-selection-api/api/v1/student/course-select/open-turns/${config.studentId}")
        .get()
        .build()
    return okHttpClient.newCall(request).execute().use {
        if (!it.isSuccessful) throw RuntimeException("getOpenedTurn Request failed, code: ${it.code}, msg: ${it.message}")

        val body = it.body!!.string()
        val result = jsonMapper.readValue<ApiResult<List<SelectTurn>>>(body)
        result.data ?: run {
            logger.debug { "resp: $body" }
            throw RuntimeException("getOpenedTurn Request failed")
        }
    }
}

fun getMajorPlan(selectTurnId: Long): MajorPlan {
    val majorPlanRequest = commonRequestBuilder
        .url("https://jwgl.sdju.edu.cn/course-selection-api/api/v1/student/course-select/major-plan/$selectTurnId/${config.studentId}")
        .get()
        .build()
    return okHttpClient.newCall(majorPlanRequest).execute().use {
        if (!it.isSuccessful) {
            logger.warn { "getMajorPlan Request failed, code: ${it.code}, msg: ${it.message}" }
            throw RuntimeException("getMajorPlan Request failed")
        }
        val body = it.body!!.string()
        val result = jsonMapper.readValue<ApiResult<MajorPlan>>(body)
        result.data ?: run {
            logger.debug { "resp: $body" }
            throw RuntimeException("getMajorPlan Request failed")
        }
    }
}

fun getLessons(selectTurnId: Long): List<Lesson> {
    val courseFilter = CourseFilter(selectTurnId, config.studentId)
    val courseFilterJson = jsonMapper.writeValueAsString(courseFilter)
    val courseFilterRequestBody = courseFilterJson.toRequestBody(("application/json").toMediaType())

    val courseFilterRequest = commonRequestBuilder
        .url("https://jwgl.sdju.edu.cn/course-selection-api/api/v1/student/course-select/query-lesson/${config.studentId}/$selectTurnId")
        .post(courseFilterRequestBody)
        .build()
    return okHttpClient.newCall(courseFilterRequest).execute().use {
        if (!it.isSuccessful) throw RuntimeException("getLessons Request failed, code: ${it.code}, msg: ${it.message}")

        val body = it.body!!.string()
        val result = jsonMapper.readValue<ApiResult<LessonQueryResult>>(body)

        result.data?.lessons ?: run {
            logger.debug { "resp: $body" }
            throw RuntimeException("getLessons Request failed")
        }
    }
}

fun grabCourse(selectTurnId: Long, lessonId: Long): Boolean {
    val addRequest = AddRequest(
        config.studentId,
        selectTurnId,
        // 可以传多个，但atomic，所以只传一个
        listOf(RequestMiddleDto(lessonId)),
        null
    )
    val addRequestJson = jsonMapper.writeValueAsString(addRequest)
    val reqIdRequestBody = addRequestJson.toRequestBody(("application/json").toMediaType())
    val reqIdRequest = commonRequestBuilder
        .url("https://jwgl.sdju.edu.cn/course-selection-api/api/v1/student/course-select/add-request")
        .post(reqIdRequestBody)
        .build()
    val reqId = okHttpClient.newCall(reqIdRequest).execute().use {
        if (!it.isSuccessful) throw RuntimeException("grabCourse Request failed, code: ${it.code}, msg: ${it.message}")

        val body = it.body?.string()
        val result = jsonMapper.readValue<ApiResult<String>>(body!!)
        result.data ?: run {
            logger.debug { "resp: $body" }
            throw RuntimeException("grabCourse Request failed")
        }
    }

    val grabCourseRequest = commonRequestBuilder
        .url("https://jwgl.sdju.edu.cn/course-selection-api/api/v1/student/course-select/add-drop-response/${config.studentId}/$reqId")
        .get()
        .build()
    val grabResult = okHttpClient.newCall(grabCourseRequest).execute().use {
        if (!it.isSuccessful) {
            logger.warn { "grabCourseRequest Request failed" }
            return false
        }
        val body = it.body?.string()
        val result = jsonMapper.readValue<ApiResult<GrabRequestResult>>(body!!)
        result.data ?: run {
            logger.debug { "grabCourseRequest body: $body" }
            null
        }
    }

    if (grabResult == null) {
        logger.debug { "GrabRequestResult is null, probably cause by too fast request" }
        return false
    }

    return if (grabResult.success) true
    else {
        logger.debug { "grabResult.errorMessage: ${grabResult.errorMessage?.text}" }
        false
    }
}