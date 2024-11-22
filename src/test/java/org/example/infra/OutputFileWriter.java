package org.example.infra;

import org.example.domain.Mountain;
import org.example.domain.Territory;

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
}
