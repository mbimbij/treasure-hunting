package org.example.infra;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InputReader {
    @SneakyThrows
    public static TerritoryData readFile(String pathString) {
        TerritoryData territoryData = new TerritoryData();
        Path path = Paths.get(pathString);
        Files.lines(path, StandardCharsets.UTF_8)
                .forEach(s -> {
                    String[] split = s.split("-");
                    territoryData.setWidth(Integer.parseInt(split[1].trim()));
                    territoryData.setHeight(Integer.parseInt(split[2].trim()));
                });
        return territoryData;
    }
}
