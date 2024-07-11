package io.sakurasou.entity

/**
 * @author ShiinaKin
 * 2024/7/8 15:51
 */
data class CourseFilter(
    val turnId: Long,
    val studentId: Long,
    val semesterId: Int,
    val pageNo: Int,
    val pageSize: Int,
    val courseId: Long?,
    val courseNameOrCode: String?,
    val lessonNameOrCode: String?,
    val teacherNameOrCode: String?,
    val week: String?,
    val grade: String?,
    val departmentId: String?,
    val majorId: String?,
    val adminclassId: String?,
    val campusId: String?,
    val openDepartmentId: String?,
    val courseTypeId: String?,
    val coursePropertyId: String?,
    val canSelect: Int,
    val _canSelect: String,
    val creditGte: Double?,
    val creditLte: Double?,
    val hasCount: Boolean?,
    val ids: List<Int>?,
    val substitutedCourseId: Int?,
    val courseSubstitutePoolId: Int?,
    val sortField: String,
    val sortType: String
) {
    constructor(
        turnId: Long,
        studentId: Long,
        courseId: Long? = null,
        semesterId: Int = 101,
        sortField: String = "course"
    ) : this(
        turnId,
        studentId,
        semesterId,
        1,
        500,
        courseId,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        1,
        "可选",
        null,
        null,
        null,
        null,
        null,
        null,
        sortField,
        "ASC"
    )
}

