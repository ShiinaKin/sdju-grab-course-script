package io.sakurasou.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.config
import io.sakurasou.course.allPlanCourse
import io.sakurasou.course.getCourseDetail
import io.sakurasou.course.getMajorPlan
import io.sakurasou.entity.PlanCourse
import io.sakurasou.scanner

/**
 * @author ShiinaKin
 * 2024/7/8 18:12
 */
object CourseUtils {
    private val logger = KotlinLogging.logger { }
    fun addCourse() {
        logger.info { "loading course list..." }
        getMajorPlan()
        while (true) {
            logger.info { "Pls input the course name you want to select, or input '0' to exit:" }
            val courseName = scanner.nextLine().trimIndent()
            if (courseName == "0") {
                break
            }
            logger.info { "filtering..." }
            val tempMap = filterCourse(allPlanCourse, courseName)
            if (tempMap.isEmpty()) {
                logger.info { "No course found" }
                continue
            }
            logger.info { "find courses(${tempMap.size}):" }
            val tempList = tempMap.entries.toList()
            tempList.forEachIndexed { index, it ->
                logger.info { "${index + 1}. ${it.value}" }
            }
            logger.info { "Pls input the index of the course you want to select:" }
            val index = scanner.nextLine().toIntOrNull()
            if (index == null || index !in 1..tempMap.size) {
                logger.info { "Invalid input" }
                continue
            }
            config.needGrabCourseMap[tempList[index - 1].key.first] = tempList[index - 1].key.second
            logger.info { "Course added" }
        }
    }

    fun filterCourse(allPlanCourse: List<PlanCourse>, courseName: String): MutableMap<Pair<Long, String>, String> {
        val tempMap = mutableMapOf<Pair<Long, String>, String>()
        allPlanCourse.forEach { planCourse ->
            if (planCourse.nameZh != null && planCourse.nameZh.contains(courseName)
                || planCourse.nameEn != null && planCourse.nameEn.contains(courseName)
            ) {
                tempMap.putAll(getCourseDetail(planCourse.id!!))
            }
        }
        return tempMap
    }

}