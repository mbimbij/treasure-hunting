package org.example;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

    /**
     * Decided on an all-args constructor instead of a Builder for example. We have all the data at once after reading
     * the file, and while builders are "elegant", in my experience they tend to hide issues when adding or removing
     * data, which happens a lot in the early phases of a project and going back and forth on the modelling of the
     * domain. Granted the call to a validation method in the build() method could possibly address this, a constructor
     * is simpler and "fails fast(-er)".
     * <p>
     * But in the end one or the other is good enough imo, i'm open to discussion, compromise and changing my tune
     * according to feedback and whether this design gets buy-in from the team or not.
     *
     * @param width
     * @param height
     * @param mountains
     * @param treasures
     */
    public Territory(int width, int height, List<Mountain> mountains, List<Treasure> treasures) {
        this.width = width;
        this.height = height;
        this.mountains = mountains;
        this.treasures = treasures;
        validate();
    }

    private void validate() {
        if (this.width <= 0 || this.height <= 0) {
            throw new IllegalArgumentException("Width and height must be greater than zero");
        }
        if(overlappingMountains()){
            throw new IllegalArgumentException("Cannot build territory because of overlapping mountains.");
        }
        if(overlappingTreasures()){
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
