package org.example.infra;

import org.example.domain.Mountain;
import org.example.domain.Player;
import org.example.domain.Simulation;
import org.example.domain.Treasure;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

public class SimulationFormatter {

    private final String newLine = System.lineSeparator();

    public String formatSimulation(Simulation simulation) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println(formatSize(simulation.getSize()));
        printWriter.println(formatMountains(simulation.getMountains()));
        printWriter.println(formatTreasures(simulation.getTreasures()));
        printWriter.println(formatPlayers(simulation.getPlayers()));
        return stringWriter.toString();
    }

    String formatSize(Simulation.Size size) {
        return "C - %d - %d".formatted(size.width(), size.height());
    }

    String formatMountain(Mountain mountain) {
        int weCoordinates = mountain.getCoordinates().westEast();
        int nsCoordinates = mountain.getCoordinates().northSouth();
        return "M - %d - %d".formatted(weCoordinates, nsCoordinates);
    }

    String formatTreasure(Treasure treasure) {
        int weCoordinates = treasure.getCoordinates().westEast();
        int nsCoordinates = treasure.getCoordinates().northSouth();
        int remainingQuantity = treasure.quantity();
        return "T - %d - %d - %d".formatted(weCoordinates, nsCoordinates, remainingQuantity);
    }

    String formatTreasures(List<Treasure> treasures) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        return treasures.stream().filter(treasure -> !treasure.isEmpty())
                .map(this::formatTreasure)
                .collect(Collectors.joining(newLine));
    }

    String formatPlayer(Player player) {
        return "A - %s - %d - %d - %s - %d".formatted(
                player.getName(),
                player.getCoordinates().westEast(),
                player.getCoordinates().northSouth(),
                OrientationData.from(player.getOrientation()),
                player.getCollectedTreasuresCount()
        );
    }

    private String formatPlayers(List<Player> players) {
        return players.stream()
                .map(this::formatPlayer)
                .collect(Collectors.joining(newLine));
    }

    private String formatMountains(List<Mountain> mountains) {
        return mountains
                .stream()
                .map(this::formatMountain)
                .collect(Collectors.joining(newLine));
    }
}
