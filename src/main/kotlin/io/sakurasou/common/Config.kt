package io.sakurasou.common

import java.util.concurrent.ConcurrentHashMap

/**
 * @author ShiinaKin
 * 2024/7/8 15:09
 */
data class Config(
    val authorization: String,
    val studentId: Long,
    val categoryAndKeyword: Map<String, Pair<Int, List<String>>>,
) {
    constructor() : this(
        "authorization",
        -1,
        mapOf(
            "通识选修" to (0 to mutableListOf()),
            "美育英语" to (0 to mutableListOf()),
            "培养方案" to (0 to mutableListOf()),
            "体育四史" to (0 to mutableListOf()),
        ),
    )
}
