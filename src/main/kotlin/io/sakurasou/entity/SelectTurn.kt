package io.sakurasou.entity

/**
 * @author ShiinaKin
 * 2024/7/11 14:33
 */
data class SelectTurn(
    val id: Long,
    val name: String,
    val bulletin: String,
    val openDateTimeText: String,
    val selectDateTimeText: String,
    val dropDateTimeText: String,
    val openDateTimeRange: DateTimeRange,
    val selectDateTimeRange: DateTimeRange,
    val dropDateTimeRange: DateTimeRange,
    val turnOpenTimes: List<Any>,
    val addRulesText: List<String>,
    val dropRulesText: List<String>,
    val turnMode: TurnMode,
    val limitCount: Int,
    val allowEnter: Boolean,
    val disallowReasons: List<String>,
    val notNowTurnDropLimitCount: Int?,
    val capPercentage: Double?,
    val conflictRatio: Double?,
    val lowestPeople: Int?
)

data class DateTimeRange(
    val startDateTime: String,
    val endDateTime: String
)

data class TurnMode(
    val enablePreSelect: Boolean,
    val enableDelayRelease: Boolean,
    val enableVirtualWallet: Boolean,
    val showCount: Boolean,
    val enableStudentPreset: Boolean
)
