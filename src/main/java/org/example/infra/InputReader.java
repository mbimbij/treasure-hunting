package org.example.infra;

import lombok.SneakyThrows;
import org.example.domain.Mountain;
import org.example.domain.Territory;
import org.example.domain.Treasure;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class InputReader {
    static final String SIZE_ALREADY_DEFINED_ERROR_MESSAGE_FORMAT = "Error reading line %s. Size already defined";

    @SneakyThrows
    public static TerritoryData readFile(String pathString) {
        TerritoryData territoryData = new TerritoryData();
        Path path = Paths.get(pathString);

        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8);) {
            lines.forEach(line -> {
                readLine(line, territoryData);
            });
        }

        return territoryData;
    }

    public static void readLine(String line, TerritoryData territoryData) {
        if (line.startsWith("C")) {
            readSize(line, territoryData);
        } else if (line.startsWith("M")) {
            readMountain(line, territoryData);
        } else {
            String[] split = line.split("-");
            Treasure treasure = new Treasure(Integer.parseInt(split[1].trim()),
                    Integer.parseInt(split[2].trim()),
                    Integer.parseInt(split[3].trim()));
            territoryData.addTreasure(treasure);
        }
    }

    private static void readSize(String line, TerritoryData territoryData) {
        if (territoryData.getSize() != null) {
            throw new IllegalArgumentException(SIZE_ALREADY_DEFINED_ERROR_MESSAGE_FORMAT.formatted(line));
        }
        String[] split = line.split("-");
        Territory.Size size = new Territory.Size(Integer.parseInt(split[1].trim()),
                Integer.parseInt(split[2].trim()));
        territoryData.setSize(size);
    }

    private static void readMountain(String line, TerritoryData territoryData) {
        String[] split = line.split("-");
        Mountain mountain = new Mountain(Integer.parseInt(split[1].trim()),
                Integer.parseInt(split[2].trim()));
        territoryData.addMountain(mountain);
    }
}
