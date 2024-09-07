package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * org.example.FileRW class for reading and writing files.
 */
public class FileRW {
    private InputStream inputStream;
    private OutputStream outputStream;
    private Path path;
    /**
     * Constructor for org.example.FileRW.
     * @param path The path to the file
     */
    public FileRW(Path path) {
        this.path = path;
    }
    /**
     * Read the file.
     * @return The contents of the file
     */
    public String read() throws IOException {
        inputStream = Files.newInputStream(path);
        byte[] bytes = inputStream.readAllBytes();
        inputStream.close();
        inputStream = null;
        return new String(bytes, StandardCharsets.UTF_8);
    }
    /**
     * (Over)write to the file.
     * @param content The content to write
     */
    public void write(String content) throws IOException {
        Files.deleteIfExists(path);
        outputStream = Files.newOutputStream(path);
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
        outputStream = null;
    }
}
