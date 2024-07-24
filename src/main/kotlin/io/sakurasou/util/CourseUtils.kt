package io.sakurasou.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.course.classifiedLessons
import io.sakurasou.course.getLessons
import io.sakurasou.course.grabCourse
import io.sakurasou.entity.GrabCourseInfo
import io.sakurasou.entity.Lesson
import io.sakurasou.entity.SelectTurn
import io.sakurasou.ioScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration

/**
 * @author ShiinaKin
 * 2024/7/8 18:12
 */
object CourseUtils {
    private val logger = KotlinLogging.logger { }

    const val KEYWORD_通识选修 = "通识选修"
    const val KEYWORD_美育英语 = "美育英语"
    const val KEYWORD_培养方案 = "培养方案"
    const val KEYWORD_体育四史 = "体育四史"

    const val CATEGORY_通识选修: String = "通识选修"
    const val CATEGORY_美育英语: String = "美育英语"
    const val CATEGORY_培养方案: String = "培养方案"
    const val CATEGORY_体育四史: String = "体育四史"

    // val categoryArr = arrayOf("通识选修", "美育英语", "培养方案", "体育四史")

    fun classifyAndFetchLesson(selectTurns: List<SelectTurn>) {
        selectTurns.forEach {
            when {
                KEYWORD_通识选修 in it.name -> {
                    classifiedLessons.add(Triple(CATEGORY_通识选修, it.id, getLessons(it.id)))
                }

                KEYWORD_美育英语 in it.name -> {
                    classifiedLessons.add(Triple(CATEGORY_美育英语, it.id, getLessons(it.id)))
                }

                KEYWORD_培养方案 in it.name -> {
                    classifiedLessons.add(Triple(CATEGORY_培养方案, it.id, getLessons(it.id)))
                }

                KEYWORD_体育四史 in it.name -> {
                    classifiedLessons.add(Triple(CATEGORY_体育四史, it.id, getLessons(it.id)))
                }
            }
        }
    }

    suspend fun grabCourse(categoryPair: Pair<String, MutableList<GrabCourseInfo>>) {
        val (categoryName, courses) = categoryPair
        if (courses.isEmpty()) {
            logger.info { "don't need to grab category: $categoryName" }
            return
        }
        val cnt = AtomicInteger(courses.size)
        val (_, turnId, lessons) = classifiedLessons.firstOrNull { it.first == categoryName } ?: run {
            logger.warn { "cannot find category: $categoryName" }
            return
        }
        val needGrabLessons = lessons.filter { lesson -> courses.any { isEquals(it, lesson) } }
        if (needGrabLessons.isEmpty()) {
            logger.info { "no lesson matched in category: $categoryName" }
            return
        }
        logger.info { "start grab category: $categoryName" }
        logger.debug {
            "need grab lessons: ${
                needGrabLessons.joinToString {
                    it.code + " " + it.course.nameZh + " " + (it.course.nameEn ?: "") + " " +
                            it.teachers.joinToString { t -> t.nameZh + " " + t.nameEn } + " " + it.course.credits
                }
            }"
        }
        while (cnt.get() > 0) {
            needGrabLessons.forEach outer@{ lesson ->
                if (cnt.get() <= 0) return@outer
                val iterator = courses.listIterator()
                while (iterator.hasNext()) {
                    val course = iterator.next()
                    if (isEquals(course, lesson)) {
                        ioScope.launch {
                            if (grabCourse(turnId, lesson.id)) {
                                cnt.getAndDecrement()
                                iterator.remove()
                                logger.info { "category: $categoryName, grab ${lesson.course.nameZh}/${lesson.course.nameEn} ${lesson.code} credits: ${lesson.course.credits} successfully" }
                            } else {
                                logger.debug { "category: $categoryName, grab ${lesson.course.nameZh}/${lesson.course.nameEn} ${lesson.code} credits: ${lesson.course.credits} failed" }
                            }
                        }
                        delay(Duration.parse("200ms"))
                    }
                }
            }
        }
        logger.info { "category: $categoryName successfully grab ${cnt.get() - courses.size} courses" }
    }

    private fun isEquals(course: GrabCourseInfo, lesson: Lesson): Boolean {
        if (course.code != null) return course.code == lesson.code

        if (course.name == null) throw RuntimeException("GrabCourseInfo must have code or name")
        val nameMatch =
            lesson.course.nameZh.contains(course.name) || (lesson.course.nameEn?.contains(course.name) == true)
        val teacherMatch = if (course.teacher != null)
            lesson.teachers.any { it.nameZh.contains(course.teacher) || it.nameEn.contains(course.teacher) }
        else true
        val creditsMatch = when {
            course.credits != null -> course.credits == lesson.course.credits
            course.minCredits != null -> course.minCredits <= lesson.course.credits
            else -> true
        }
        return nameMatch && teacherMatch && creditsMatch
    }
}