package org.example

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
/**
 * A very simple class for reading and writing files.
 * @param path The path to the file.
 */
class FileRW(private var path: Path) {
    /**
     * Read the contents of the file.
     * @return The contents of the file as a string.
     */
    fun read(): String {
        val inputStream: InputStream = Files.newInputStream(path)
        val bytes : ByteArray = inputStream.readAllBytes()
        inputStream.close()
        return String(bytes, Charsets.UTF_8)
    }
    /**
     * Write content to the file. If the file already exists, it will be deleted.
     * @param content The content to write to the file.
     */
    fun write(content: String) {
        Files.deleteIfExists(path)
        val outputStream: OutputStream = Files.newOutputStream(path)
        outputStream.write(content.toByteArray(Charsets.UTF_8))
        outputStream.flush()
        outputStream.close()
    }
}