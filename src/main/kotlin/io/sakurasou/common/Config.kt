package io.sakurasou.common

import java.util.concurrent.ConcurrentHashMap

/**
 * @author ShiinaKin
 * 2024/7/8 15:09
 */
data class Config(
    val authorization: String,
    val cookie: String,
    val selectTurnId: Int,
    val studentId: Long,
    val needGrabCourseMap: ConcurrentHashMap<Long, String>
)
