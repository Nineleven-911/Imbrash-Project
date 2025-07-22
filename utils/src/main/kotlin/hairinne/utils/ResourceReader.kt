package hairinne.utils

import java.nio.charset.Charset

object ResourceReader {
    fun readFile(path: String, charset: Charset = Charsets.UTF_8): String {
        return javaClass.classLoader.getResource(path)?.readText(charset) ?: ""
    }
}