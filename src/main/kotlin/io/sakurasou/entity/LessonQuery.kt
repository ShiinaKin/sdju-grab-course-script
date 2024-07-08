package io.sakurasou.entity

/**
 * @author ShiinaKin
 * 2024/7/8 16:03
 */
data class LessonQueryResult(
    val lessons: List<Lesson>,
    val pageInfo: PageInfo,
    val sort__: Sort
)

data class PageInfo(
    val currentPage: Int,
    val rowsInPage: Int,
    val rowsPerPage: Int,
    val totalRows: Int,
    val totalPages: Int
)

data class Sort(
    val field: String,
    val type: String
)