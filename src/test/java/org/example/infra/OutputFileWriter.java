package org.example.infra;

import org.example.domain.Mountain;
import org.example.domain.Territory;
import org.example.domain.Treasure;

import java.nio.file.Path;

public class OutputFileWriter {
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
}
