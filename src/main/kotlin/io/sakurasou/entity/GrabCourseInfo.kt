package io.sakurasou.entity

/**
 * @author ShiinaKin
 * 2024/7/24 17:14
 */
data class GrabCourseInfo(
    val code: String? = null,
    val name: String? = null,
    val teacher: String? = null,
    val minCredits: Double? = null,
    val credits: Double? = null
) {
    init {
        if (code == null && name == null) {
            throw RuntimeException("GrabCourseInfo must have code or name")
        }
    }
}
