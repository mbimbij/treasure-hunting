package org.example.infra;

import org.example.domain.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class OutputFileWriter {

    private final String newLine = System.lineSeparator();

    public void writeToFile(Territory territory, Path file) {

    }

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
}
