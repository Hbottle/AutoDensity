package com.bottle.core.utils

import org.w3c.dom.Document
import java.io.*
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * 向一个文件里面写一段文本
 * @param file 输出的文件
 * @param content 要写入文件的文本内容
 * @param encode  传null，或者空，则使用默认UTF-8
 */
fun writeFile(file: File, content: String) {
    var writer: BufferedWriter? = null
    val write: OutputStreamWriter
    val fs: FileOutputStream
    try {
        val parent = file.parentFile
        if (parent != null && !parent.exists()) {
            parent.mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        fs = FileOutputStream(file)
        write = OutputStreamWriter(fs, "UTF-8")
        writer = BufferedWriter(write)
        writer.write(content)
        writer.flush()
    } finally {
        close(writer)
    }
}

fun close(closeable: Closeable?) {
    try {
        closeable?.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 将一个Document对象写入到xml文件
 * @param doc
 * @param filePath
 * @throws Exception
 */
@Throws(Exception::class)
fun domToXmlFile(doc: Document, filePath: String) {
    var pw: PrintWriter? = null
    try {
        val tf = TransformerFactory.newInstance()
        val transformer: Transformer
        transformer = tf.newTransformer()
        val source = DOMSource(doc)
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        pw = PrintWriter(File(filePath))
        val streamResult = StreamResult(pw)
        transformer.transform(source, streamResult)
        println(filePath)
    } catch (e: Exception) {
        throw e
    } finally {
        close(pw)
    }
}

@Throws(java.lang.Exception::class)
fun getFileSizes(f: File): Long { // 取得文件大小
    var size: Long = 0
    if (f.exists()) {
        val fis = FileInputStream(f)
        size = fis.available().toLong()
        close(fis)
    } else {
        println("文件不存在")
    }
    return size
}

