package io.sakurasou.common

/**
 * @author ShiinaKin
 * 2024/7/8 15:01
 */
data class ApiResult<T>(
    val result: Int,
    val message: String?,
    val data: T?
)
