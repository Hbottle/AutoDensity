package com.bottle.core.arch.smallest

import com.bottle.core.utils.domToXmlFile
import com.bottle.core.utils.writeFile
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.math.BigDecimal
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory

/**
 * 最大值
 */
private const val MAX_VALUE = 720

/**
 * 设计稿尺寸(将自己设计师的设计稿的宽度填入)
 */
private const val DESIGN_WIDTH = 375

/**
 * 设计稿的高度  （将自己设计师的设计稿的高度填入）
 */
private const val DESIGN_HEIGHT = 667

/**
 * 执行这个方法，将会在core模块的res目录下生成一系列的values-sw的dimens.xml
 */
fun main(args: Array<String>) {
   val generate = true
    if (generate) {
        generate()
    } else {
        layoutFileCompat()
    }
}

fun generate() {
    val smallest = DESIGN_WIDTH.coerceAtMost(DESIGN_HEIGHT)
    val values = DimenTypes.values()
    var sb: StringBuilder
    val rootPath = File("")
    val basePath = rootPath.absolutePath
    for (value in values) {
        sb = StringBuilder()
        sb.append(basePath).append(File.separator)
            .append("core").append(File.separator)
            .append("src").append(File.separator)
            .append("main").append(File.separator)
            .append("res")
        val path = sb.toString()
        makeAll(smallest, value, path, "values-sw${value.smallestWith}dp")
    }
}

fun makeAll(sw: Int, dimen: DimenTypes, resPath: String, folder: String) {
    val valueFile = File(resPath + File.separator + folder)
    if (!valueFile.exists()) {
        valueFile.mkdirs()
    }
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
    val root = document.createElement("resources")
    val power = dimen.smallestWith / (sw * 1.0F)
    for (i in 1..MAX_VALUE) {
        val dimenElement = document.createElement("dimen")
        val dpValue = (i * power).toDouble()
        val bigDecimal = BigDecimal(dpValue)
        val finDp = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toFloat()
        dimenElement.textContent = "${finDp}dp"
        dimenElement.setAttribute("name", "dp$i")
        root.appendChild(dimenElement)
    }
    document.appendChild(root)
    val dimenFile = resPath + File.separator + folder + File.separator + "dimens.xml"
    domToXmlFile(document, dimenFile)
}


private const val regDimen = "@dimen/dp"

/**
 * 将现有的布局文件中的xydp替换成dpxy，慎重
 */
fun layoutFileCompat() {
    var file = File("") // 根目录
    val filePath = file.absolutePath
    file = File(filePath)
    val modules = file.list()
    if (modules == null || modules.isEmpty()) {
        println("目录不存在")
        return
    }
    var temp: File
    var sb: StringBuilder
    val basePath = file.absolutePath
    for (module in modules) {
        sb = StringBuilder()
        sb.append(basePath).append(File.separator)
            .append(module).append(File.separator)
            .append("src").append(File.separator)
            .append("main").append(File.separator)
            .append("res").append(File.separator)
            .append("layout")
        val layoutPath = sb.toString()
        temp = File(layoutPath)
        if (!temp.exists()) {
            continue
        }
        val layoutFiles = temp.listFiles()
        for (layoutFile in layoutFiles) {
            if (layoutFile != null) {
                resetLayoutFileDimens(layoutFile, "UTF-8")
            }
        }
    }
}

fun resetLayoutFileDimens(file: File, encode: String?) {
    if (!file.exists() || file.isDirectory) {
        return
    }
    var bReader: BufferedReader? = null
    val fs: FileInputStream
    val ir: InputStreamReader
    val sb = StringBuilder()
    try {
        fs = FileInputStream(file)
        ir = InputStreamReader(fs, encode)
        bReader = BufferedReader(ir)
        var line = bReader.readLine()
        while (line != null) {
            sb.append(replace(line)).append("\n")
            line = bReader.readLine()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            bReader!!.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    try {
        writeFile(file, sb.toString())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun replace(line: String): String {
    /**
     * 匹配:"数字.(0个或1个.)数字(0个或多个数字)dp"
     */
    val regex = "\"\\d+.?\\d*(dp\")"
    val p = Pattern.compile(regex)
    val m = p.matcher(line)
    var temp = line
    while (m.find()) {
        val dpValue = line.substring(m.start() + 1, m.end() - 3)
        temp = line.replace(
            line.substring(m.start() + 1, m.end() - 1), regDimen + dpValue
        )
        println(line)
        println(temp)
    }
    return temp
}