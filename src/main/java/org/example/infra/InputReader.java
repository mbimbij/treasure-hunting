package org.example.infra;

import lombok.SneakyThrows;
import org.example.domain.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class InputReader {
    static final String SIZE_ALREADY_DEFINED_ERROR_MESSAGE_FORMAT = "Error reading line %s. Size already defined";

    @SneakyThrows
    public static SimulationBuilder readFile(String pathString) {
        SimulationBuilder simulationBuilder = new SimulationBuilder();
        Path path = Paths.get(pathString);

        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8);) {
            lines.forEach(line -> {
                readLine(line, simulationBuilder);
            });
        }

        return simulationBuilder;
    }

    static void readLine(String line, SimulationBuilder simulationData) {
        if (line.startsWith("C")) {
            readSize(line, simulationData);
        } else if (line.startsWith("M")) {
            readMountain(line, simulationData);
        } else if (line.startsWith("T")) {
            readTreasure(line, simulationData);
        } else if (line.startsWith("A")) {
            readPlayer(line, simulationData);
        }
    }

    private static void readPlayer(String line, SimulationBuilder simulationData) {
        String[] split = line.split("-");
        String name = split[1].trim();
        Coordinates coordinates = readCoordinates(split[2], split[3]);
        Orientation orientation = readOrientation(split[4]);
        List<Command> commands = readCommands(split[5]);
        Player player = new Player(name, coordinates, orientation, commands);
        simulationData.addPlayer(player);
    }

    private static Orientation readOrientation(String orientationString) {
        OrientationData orientationInput = OrientationData.valueOf(orientationString.trim());
        return orientationInput.toDomainValue();
    }

    private static List<Command> readCommands(String commandsString) {
        String[] commandsStrings = commandsString.trim().split("");
        return Arrays.stream(commandsStrings)
                .map(Command::valueOf)
                .toList();
    }

    private static void readTreasure(String line, SimulationBuilder simulationData) {
        String[] split = line.split("-");
        int weCoordinates = Integer.parseInt(split[1].trim());
        int nsCoordinates = Integer.parseInt(split[2].trim());
        int quantity = Integer.parseInt(split[3].trim());
        Treasure treasure = new Treasure(weCoordinates, nsCoordinates, quantity);
        simulationData.addTreasure(treasure);
    }

    private static void readSize(String line, SimulationBuilder simulationData) {
        if (simulationData.getSize() != null) {
            throw new IllegalArgumentException(SIZE_ALREADY_DEFINED_ERROR_MESSAGE_FORMAT.formatted(line));
        }
        String[] split = line.split("-");
        int width = Integer.parseInt(split[1].trim());
        int height = Integer.parseInt(split[2].trim());
        simulationData.withSize(width, height);
    }

    private static void readMountain(String line, SimulationBuilder simulationData) {
        String[] split = line.split("-");
        Mountain mountain = new Mountain(readCoordinates(split[1], split[2]));
        simulationData.addMountain(mountain);
    }

    private static Coordinates readCoordinates(String weCoordinates, String nsCoordinates) {
        return new Coordinates(Integer.parseInt(weCoordinates.trim()),
                Integer.parseInt(nsCoordinates.trim()));
    }
}
