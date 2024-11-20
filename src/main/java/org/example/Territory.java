package org.example;

import lombok.Getter;

import java.util.List;

/**
 * Represent 'Madre de Dios' territory. I opted for a noun for the class name and 'Madre de Dios' for the instance.
 * name. "Carte" translates to "Map", and I didn't want to have potential collisions with java.util.Map, so I decided on
 * "Territory".
 */
@Getter
public class Territory {
    private final int width;
    private final int height;
    private final List<Mountain> mountains;
    private final List<Treasure> treasures;
    private final List<Adventurer> adventurers;

    public Territory(int width, int height, List<Mountain> mountains, List<Treasure> treasures, List<Adventurer> adventurers) {
        this.width = width;
        this.height = height;
        this.mountains = mountains;
        this.treasures = treasures;
        this.adventurers = adventurers;
        validate();
    }

    private void validate() {
        if (this.width <= 0 || this.height <= 0) {
            throw new IllegalArgumentException("Width and height must be greater than zero");
        }
        if (overlappingMountains()) {
            throw new IllegalArgumentException("Cannot build territory because of overlapping mountains.");
        }
        if (overlappingTreasures()) {
            throw new IllegalArgumentException("Cannot build territory because of overlapping treasures.");
        }
    }

    private boolean overlappingMountains() {
        List<Coordinates> mountainsCoordinates = this.mountains.stream().map(Mountain::coordinates).toList();
        return mountainsCoordinates.stream().distinct().count() < mountainsCoordinates.size();
    }

    private boolean overlappingTreasures() {
        List<Coordinates> treasuresCoordinates = this.treasures.stream().map(Treasure::coordinates).toList();
        return treasuresCoordinates.stream().distinct().count() < treasuresCoordinates.size();
    }
}
