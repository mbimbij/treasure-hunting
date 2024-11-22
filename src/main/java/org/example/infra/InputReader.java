package org.example.infra;

import lombok.SneakyThrows;
import org.example.domain.Territory;

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
                    Territory.Size size = new Territory.Size(Integer.parseInt(split[1].trim()),
                            Integer.parseInt(split[2].trim()));
                    territoryData.setSize(size);
                });
        return territoryData;
    }
}
