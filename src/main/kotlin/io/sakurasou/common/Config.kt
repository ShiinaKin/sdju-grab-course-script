package io.sakurasou.common

import io.sakurasou.entity.GrabCourseInfo

/**
 * @author ShiinaKin
 * 2024/7/8 15:09
 */
data class Config(
    val authorization: String,
    val cookie: String,
    val studentId: Long,
    val categoryCourseMap: Map<String, MutableList<GrabCourseInfo>>,
) {
    constructor() : this(
        authorization = "authorization",
        cookie = "cookie",
        studentId = -1,
        categoryCourseMap = mapOf(
            "通识选修" to mutableListOf(),
            "美育英语" to mutableListOf(),
            "培养方案" to mutableListOf(),
            "体育四史" to mutableListOf(),
        ),
    )
}