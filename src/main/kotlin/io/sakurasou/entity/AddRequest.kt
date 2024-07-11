package io.sakurasou.entity

/**
 * @author ShiinaKin
 * 2024/7/8 15:27
 */
data class AddRequest(
    val studentAssoc: Long,
    val courseSelectTurnAssoc: Long,
    val requestMiddleDtos: List<RequestMiddleDto>,
    val coursePackAssoc: Int?
)

data class RequestMiddleDto(
    val lessonAssoc: Long,
    val virtualCost: Any?
) {
    constructor(lessonAssoc: Long) : this(lessonAssoc, null)
}