package io.sakurasou.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.config
import io.sakurasou.course.classifiedLessons
import io.sakurasou.course.effectiveCategoryCourseMap
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

    fun classifyAndFetchLesson(selectTurns: List<SelectTurn>) {
        val keys = config.categoryCourseMap.keys
        val unmatched = mutableListOf<String>()
        val selectTurnMutableList = selectTurns.toMutableList()
        keys.forEach outer@ { key ->
            val iterator = selectTurnMutableList.iterator()
            while (iterator.hasNext()) {
                val selectTurn = iterator.next()
                if (key in selectTurn.name) {
                    classifiedLessons.add(Triple(key, selectTurn.id, getLessons(selectTurn.id)))
                    effectiveCategoryCourseMap[key] = config.categoryCourseMap[key]!!
                    iterator.remove()
                    return@outer
                }
            }
            unmatched.add(key)
        }
        if (unmatched.isNotEmpty()) {
            logger.info { "unmatched turns(probably cause of duplicate category name): $unmatched" }
        }
    }

    suspend fun grabCourse(categoryPair: Pair<String, MutableList<GrabCourseInfo>>) {
        val (categoryName, courses) = categoryPair
        if (courses.isEmpty()) {
            logger.info { "don't need to grab category: $categoryName" }
            return
        }
        val originCourseSize = courses.size
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
        logger.info { "category: $categoryName successfully grab ${originCourseSize - cnt.get()} courses" }
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