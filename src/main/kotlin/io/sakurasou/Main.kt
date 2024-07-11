package io.sakurasou

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.common.Config
import io.sakurasou.course.getOpenedTurn
import io.sakurasou.course.isSelectStart
import io.sakurasou.util.CourseUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import okhttp3.Request
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * @author ShiinaKin
 * 2024/7/8 14:58
 */

const val CONFIG_FILE_PATH = "./config.yaml"

val ioScope = CoroutineScope(Dispatchers.IO)
val scanner = Scanner(System.`in`, "GBK")

val yamlMapper: ObjectMapper = YAMLMapper().registerModules(kotlinModule(), JavaTimeModule())

val threadPool: ExecutorService = Executors.newFixedThreadPool(4)

val configFile: File = FileUtils.getFile(CONFIG_FILE_PATH)
lateinit var config: Config
lateinit var commonRequestBuilder: Request.Builder

val logger = KotlinLogging.logger { }

fun main() {
    init()
    menu()
}

fun menu() {
    logger.info {
        """
  .--.--.     ,---,                                                ,--/  /|                      
 /  /    '. ,--.' |      ,--,    ,--,                           ,---,': / '  ,--,                
|  :  /`. / |  |  :    ,--.'|  ,--.'|         ,---,             :   : '/ / ,--.'|         ,---,  
;  |  |--`  :  :  :    |  |,   |  |,      ,-+-. /  |            |   '   ,  |  |,      ,-+-. /  | 
|  :  ;_    :  |  |,--.`--'_   `--'_     ,--.'|'   |  ,--.--.   '   |  /   `--'_     ,--.'|'   | 
 \  \    `. |  :  '   |,' ,'|  ,' ,'|   |   |  ,"' | /       \  |   ;  ;   ,' ,'|   |   |  ,"' | 
  `----.   \|  |   /' :'  | |  '  | |   |   | /  | |.--.  .-. | :   '   \  '  | |   |   | /  | | 
  __ \  \  |'  :  | | ||  | :  |  | :   |   | |  | | \__\/: . . |   |    ' |  | :   |   | |  | | 
 /  /`--'  /|  |  ' | :'  : |__'  : |__ |   | |  |/  ," .--.; | '   : |.  \'  : |__ |   | |  |/  
'--'.     / |  :  :_:,'|  | '.'|  | '.'||   | |--'  /  /  ,.  | |   | '_\.'|  | '.'||   | |--'   
  `--'---'  |  | ,'    ;  :    ;  :    ;|   |/     ;  :   .'   \'   : |    ;  :    ;|   |/       
            `--''      |  ,   /|  ,   / '---'      |  ,     .-./;   |,'    |  ,   / '---'        
                        ---`-'  ---`-'              `--`---'    '---'       ---`-'               
Copyright (C) 2024  ShiinaKin
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
        """
    }
    while (true) {
        logger.info {
            """
                =====Menu=====
                1. Grab courses
                2. Refresh config
                0. Exit
                ==============
            """
        }

        when (scanner.nextLine().trimIndent()) {
            "1" -> {
                logger.info { "Start grab courses infinity" }
                grab()
                logger.info { "Grab courses finished" }
            }

            "2" -> {
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

fun grab() {
    try {
        while (!isSelectStart()) Thread.sleep(Duration.parse("200ms").toJavaDuration())
        logger.info { "Select start" }
        val openedTurns = getOpenedTurn()
        logger.info { "openedTurns: ${openedTurns.joinToString(", ") { it.name }}" }
        CourseUtils.classifyAndFetchLesson(openedTurns)
        logger.info { "grabbing..." }
        val countDownLatch = CountDownLatch(4)
        CourseUtils.categoryArr.map {
            threadPool.execute {
                var retryTimes = 3
                runBlocking {
                    while (retryTimes > 0) {
                        try {
                            CourseUtils.grabCourse(it)
                            retryTimes = 0
                        } catch (e: Exception) {
                            retryTimes--
                            if (retryTimes > 0) delay(Duration.parse("1s"))
                            else throw e
                        } finally {
                            countDownLatch.countDown()
                        }
                    }
                }
            }
        }
        countDownLatch.await()
    } catch (e: Exception) {
        logger.error(e) { "Unexpected Exception: " }
    }
}

fun init() {
    config = initConfig()
    commonRequestBuilder = Request.Builder().headers(
        Headers.headersOf(
            "Authorization",
            config.authorization,
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:127.0) Gecko/20100101 Firefox/127.0 Chrome/126.0.0.0 Safari/537.36",
            "Host",
            "jwgl.sdju.edu.cn",
            "Origin",
            "https://jwgl.sdju.edu.cn",
            "Referer",
            "https://jwgl.sdju.edu.cn/course-selection/",
        )
    )
}

private fun initConfig(): Config {
    if (configFile.exists()) {
        val config = yamlMapper.readValue(configFile, Config::class.java)
        return config
    }
    val config = Config()
    configFile.createNewFile()
    FileUtils.writeStringToFile(configFile, yamlMapper.writeValueAsString(config), "UTF-8")
    return config
}

private fun saveConfig() {
    val configYaml = yamlMapper.writeValueAsString(config)
    FileUtils.writeStringToFile(configFile, configYaml, StandardCharsets.UTF_8)
}