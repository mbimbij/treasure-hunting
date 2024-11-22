package org.example.infra;

import org.example.domain.Mountain;
import org.example.domain.Player;
import org.example.domain.Territory;
import org.example.domain.Treasure;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

public class TerritoryFormatter {

    private final String newLine = System.lineSeparator();

    public String formatSize(Territory.Size size) {
        return "C - %d - %d".formatted(size.width(), size.height());
    }

    public String formatMountain(Mountain mountain) {
        int weCoordinates = mountain.coordinates().westEast();
        int nsCoordinates = mountain.coordinates().northSouth();
        return "M - %d - %d".formatted(weCoordinates, nsCoordinates);
    }

    public String formatTreasure(Treasure treasure) {
        int weCoordinates = treasure.coordinates().westEast();
        int nsCoordinates = treasure.coordinates().northSouth();
        int remainingQuantity = treasure.quantity();
        return "T - %d - %d - %d".formatted(weCoordinates, nsCoordinates, remainingQuantity);
    }

    public String formatTreasures(List<Treasure> treasures) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        return treasures.stream().filter(treasure -> !treasure.isEmpty())
                .map(this::formatTreasure)
                .collect(Collectors.joining(newLine));
    }

    public String formatPlayer(Player player) {
        return "A - %s - %d - %d - %s - %d".formatted(
                player.getName(),
                player.coordinates().westEast(),
                player.coordinates().northSouth(),
                OrientationData.from(player.getOrientation()),
                player.getCollectedTreasuresCount()
        );
    }

    public String formatTerritory(Territory territory) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println(formatSize(new Territory.Size(territory.getWidth(), territory.getHeight())));
        printWriter.println(formatMountains(territory.getMountains()));
        printWriter.println(formatTreasures(territory.getTreasures()));
        printWriter.println(formatPlayers(territory.getPlayers()));
        return stringWriter.toString();
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
