package io.sakurasou

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.common.Config
import io.sakurasou.course.grabCourses
import io.sakurasou.util.CourseUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import okhttp3.Request
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.Scanner
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.exitProcess

/**
 * @author ShiinaKin
 * 2024/7/8 14:58
 */

const val CONFIG_FILE_PATH = "./config.yaml"

val ioScope = CoroutineScope(Dispatchers.IO)
val scanner = Scanner(System.`in`, "GBK")

val yamlMapper: ObjectMapper = YAMLMapper().registerModules(kotlinModule(), JavaTimeModule())

val configFile: File = FileUtils.getFile(CONFIG_FILE_PATH)
lateinit var config: Config
lateinit var commonRequestBuilder: Request.Builder

val logger = KotlinLogging.logger { }

fun main() {
    init()
    menu()
}

fun menu() {
    while (true) {
        logger.info {
            """
                =====Menu=====
                1. Inspect need grab courses
                2. Select courses
                3. Grab courses
                4. Infinite Grab
                5. Refresh config
                0. Exit
                ==============
            """
        }

        when (scanner.nextLine().trimIndent()) {
            "1" -> {
                logger.info { "CourseId\t\tCourseDesc" }
                config.needGrabCourseMap.forEach { (key, value) ->
                    logger.info { "$key\t\t$value" }
                }
            }

            "2" -> {
                CourseUtils.addCourse()
                saveConfig()
            }

            "3" -> {
                logger.info { "Start grab courses" }
                runBlocking {
                    grabCourses()
                }
                logger.info { "Grab courses finished, ${config.needGrabCourseMap.size} courses failed" }
            }

            "4" -> {
                logger.info { "Start grab courses infinity" }
                runBlocking {
                    grabCourses(Int.MAX_VALUE)
                }
                logger.info { "Grab courses finished, ${config.needGrabCourseMap.size} courses failed" }
            }

            "5" -> {
                init()
                logger.info { "Refresh config success" }
            }

            "0" -> {
                saveConfig()
                exitProcess(0)
            }

            else -> logger.warn { "Invalid input" }
        }
    }
}

fun init() {
    config = initConfig()
    commonRequestBuilder = Request.Builder().headers(
        Headers.headersOf(
            "Authorization", config.authorization,
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:127.0) Gecko/20100101 Firefox/127.0 Chrome/126.0.0.0 Safari/537.36",
            "Host", "jwgl.sdju.edu.cn",
            "Origin", "https://jwgl.sdju.edu.cn",
            "Referer", "https://jwgl.sdju.edu.cn/course-selection/",
            "Cookie", config.cookie
        )
    )
}

private fun initConfig(): Config {
    if (configFile.exists()) {
        val config = yamlMapper.readValue(configFile, Config::class.java)
        return config
    }
    val config = Config("", "", 0, 0, ConcurrentHashMap())
    configFile.createNewFile()
    FileUtils.writeStringToFile(configFile, yamlMapper.writeValueAsString(config), "UTF-8")
    return config
}

private fun saveConfig() {
    val configYaml = yamlMapper.writeValueAsString(config)
    FileUtils.writeStringToFile(configFile, configYaml, StandardCharsets.UTF_8)
}