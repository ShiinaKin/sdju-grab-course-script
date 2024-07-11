package io.sakurasou.exception

/**
 * @author ShiinaKin
 * 2024/7/12 05:03
 */
data class CustomizeException(
    override val message: String,
    val code: Int = 10000,
    val desc: String,
) : RuntimeException(message)