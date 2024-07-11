package io.sakurasou.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.config
import io.sakurasou.course.classifiedLessons
import io.sakurasou.course.getLessons
import io.sakurasou.course.grabCourse
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

    val countMap = mapOf(
        "通识选修" to AtomicInteger(config.categoryConfigMap["通识选修"]?.count ?: 0),
        "美育英语" to AtomicInteger(config.categoryConfigMap["美育英语"]?.count ?: 0),
        "培养方案" to AtomicInteger(config.categoryConfigMap["培养方案"]?.count ?: 0),
        "体育四史" to AtomicInteger(config.categoryConfigMap["体育四史"]?.count ?: 0),
    )

    val categoryArr = arrayOf("通识选修", "美育英语", "培养方案", "体育四史")

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

    suspend fun grabCourse(categoryName: String) {
        val cnt = countMap[categoryName] ?: run {
            logger.warn { "cannot find count for category: $categoryName" }
            return
        }
        if (cnt.get() <= 0) {
            logger.info { "don't need to grab category: $categoryName" }
            return
        }
        val cfg = config.categoryConfigMap[categoryName] ?: run {
            logger.warn { "cannot find keyword for category: $categoryName" }
            return
        }
        val minCredits = cfg.minCredits
        val keywords = cfg.keywords
        val (_, turnId, lessons) = classifiedLessons.firstOrNull { it.first == categoryName } ?: run {
            logger.warn { "cannot find category: $categoryName" }
            return
        }
        logger.info { "start grab category: $categoryName" }
        while (cnt.get() > 0) {
            lessons.forEach outer@{ lesson ->
                keywords.forEach inner@{ keyword ->
                    if (cnt.get() <= 0) return@outer
                    if ((lesson.course.nameZh.contains(keyword)
                                || lesson.course.nameEn != null && lesson.course.nameEn.contains(keyword))
                        && lesson.course.credits >= minCredits
                    ) {
                        ioScope.launch {
                            if (grabCourse(turnId, lesson.id)) {
                                cnt.getAndDecrement()
                                logger.info { "category: $categoryName, grab ${lesson.course.nameZh}/${lesson.course.nameEn} ${lesson.code} credits: ${lesson.course.credits} successfully" }
                            } else {
                                logger.debug { "category: $categoryName, grab ${lesson.course.nameZh}/${lesson.course.nameEn} ${lesson.code} credits: ${lesson.course.credits} failed" }
                            }
                        }
                        delay(Duration.parse("150ms"))
                    }
                }
            }
        }
        logger.info { "category: $categoryName successfully grab ${cfg.count} course" }
    }

}