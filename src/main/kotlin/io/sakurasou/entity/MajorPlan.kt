package io.sakurasou.entity

/**
 * @author ShiinaKin
 * 2024/7/8 16:54
 */
data class MajorPlan(
    val id: Long?,
    val programId: Long?,
    val beginSemesterId: Int?,
    val currentTerm: String?,
    val term: String?,
    val programNameZh: String?,
    val programNameEn: String?,
    val cultivateTypeZh: String?,
    val cultivateTypeEn: String?,
    val courseModules: List<CourseModule>?
)

data class CourseModule(
    val id: Long?,
    val programId: Int?,
    val typeNameZh: String?,
    val typeNameEn: String?,
    val programNameZh: String?,
    val programNameEn: String?,
    val beginSemesterId: Int?,
    val parentId: Int?,
    val index: Int?,
    val reference: Boolean?,
    val requireCredits: Double?,
    val requireModuleNum: Int?,
    val children: List<CourseModule>?,
    val planCourses: List<PlanCourse>?
)

data class PlanCourse(
    val id: Long?,
    val nameZh: String?,
    val nameEn: String?,
    val code: String?,
    val credits: Double?,
    val flags: List<String>?,
    val department: Department?,
    val planCourseId: Long?,
    val compulsory: Boolean?,
    val suggestTermsText: String?,
    val programNameZh: String?,
    val programNameEn: String?,
    val courseModuleId: Long?,
    val programId: Long?,
    val planCourseSuggestTermsText: String?,
    val marks: List<Any>?,
    val flag: String?,
    val substituteCourseProfileVms: List<Any>?,
    val open: Int?
)


fun MajorPlan.flat(): List<PlanCourse> {
    val tempList = mutableListOf<PlanCourse>()
    fun dfs(courseModule: CourseModule) {
        if (courseModule.children.isNullOrEmpty()) {
            if (courseModule.planCourses != null) {
                tempList.addAll(courseModule.planCourses)
            }
            return
        }
        courseModule.children.forEach { dfs(it) }
    }
    courseModules?.forEach { dfs(it) }
    return tempList
}
