package io.sakurasou.common

/**
 * @author ShiinaKin
 * 2024/7/8 15:09
 */
data class Config(
    val authorization: String,
    val studentId: Long,
    val categoryConfigMap: Map<String, CategoryConfig>,
) {
    constructor() : this(
        "authorization",
        -1,
        mapOf(
            "通识选修" to CategoryConfig(),
            "美育英语" to CategoryConfig(),
            "培养方案" to CategoryConfig(),
            "体育四史" to CategoryConfig(),
        ),
    )
}

data class CategoryConfig(
    val count: Int,
    val minCredits: Double,
    val keywords: List<String>,
) {
    constructor() : this(0, 2.0, mutableListOf())
}
