package org.example.infra;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OutputWriter {
    @SneakyThrows
    public static void writeToFile(String formatterTerritory, String pathString) {
        System.out.printf("printing the simulation results to %s%n", pathString);
        System.out.println(formatterTerritory);
        Path path = Paths.get(pathString);
        setupAndVerifyOutputFile(path);
        Files.writeString(path, formatterTerritory, StandardCharsets.UTF_8);
    }
    private static void setupAndVerifyOutputFile(Path path) throws IOException {
        Files.deleteIfExists(path);
        Files.createFile(path);
    }
}
