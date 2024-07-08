package io.sakurasou.entity

/**
 * @author ShiinaKin
 * 2024/7/8 15:36
 */
data class GrabRequestResult(
    val id: String,
    val requestId: String,
    val exception: String?,
    val errorMessage: ErrorMessage?,
    val success: Boolean,
    val resend: Boolean
)

data class ErrorMessage(
    val textZh: String?,
    val textEn: String?,
    val text: String?,
)
