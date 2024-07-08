package io.sakurasou.entity

/**
 * @author ShiinaKin
 * 2024/7/8 16:02
 */
data class Lesson(
    val id: Long,
    val nameZh: String,
    val nameEn: String?,
    val code: String,
    val teachers: List<Teacher>,
    val course: Course,
    val minorCourse: Course?,
    val courseType: CourseType,
    val examMode: ExamMode,
    val courseProperty: CourseProperty,
    val campus: Campus,
    val limitCount: Int,
    val dateTimePlace: DateTimePlace,
    val lessonKind: LessonKind,
    val coursePackAssoc: Any?, // Define the type based on your requirements
    val selectionRemark: String?,
    val openDepartment: Department,
    val teachLang: TeachLang,
    val totalPeriod: Int,
    val compulsorys: List<String>,
    val scheduleGroups: List<ScheduleGroup>,
    val scheduleStartWeek: Int,
    val scheduleEndWeek: Int,
    val weekDays: List<Int>
)

data class Teacher(
    val id: Long,
    val nameZh: String,
    val nameEn: String
)

data class Course(
    val id: Long,
    val nameZh: String,
    val nameEn: String?,
    val code: String,
    val credits: Double,
    val flags: List<String>,
    val department: Department
)

data class CourseType(
    val id: Int,
    val nameZh: String,
    val nameEn: String?,
    val code: String
)

data class ExamMode(
    val id: Int,
    val nameZh: String,
    val nameEn: String?,
    val code: String
)

data class CourseProperty(
    val id: Int,
    val nameZh: String,
    val nameEn: String?,
    val code: String
)

data class Campus(
    val id: Int,
    val nameZh: String,
    val nameEn: String?
)

data class DateTimePlace(
    val textZh: String?,
    val textEn: String?,
    val text: String?
)

data class LessonKind(
    val id: Int,
    val nameZh: String,
    val nameEn: String,
    val code: String
)

data class Department(
    val id: Int,
    val nameZh: String,
    val nameEn: String?,
    val code: String,
    val telephone: String?
)

data class TeachLang(
    val id: Int,
    val nameZh: String,
    val nameEn: String?,
    val code: String
)

data class ScheduleGroup(
    val id: Int,
    val no: Int,
    val limitCount: Int,
    val dateTimePlace: DateTimePlace,
    val schedules: List<Schedule>,
    val default: Boolean
)

data class Schedule(
    val lessonId: Int,
    val scheduleGroupId: Int,
    val weekday: Int,
    val startUnit: Int,
    val endUnit: Int,
    val startTime: Int,
    val entTime: Int
)