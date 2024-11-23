package org.example.infra;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OutputWriter {
    @SneakyThrows
    public static void writeToFile(String formattedSimulation, String pathString) {
        System.out.printf("printing the simulation results to %s%n", pathString);
        System.out.println(formattedSimulation);
        Path path = Paths.get(pathString);
        setupOutputFile(path);
        Files.writeString(path, formattedSimulation, StandardCharsets.UTF_8);
    }
    private static void setupOutputFile(Path path) throws IOException {
        Files.deleteIfExists(path);
        Files.createFile(path);
    }
}
