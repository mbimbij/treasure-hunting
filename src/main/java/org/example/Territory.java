package org.example;

import com.speedment.common.mapstream.MapStream;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

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
        validateTerritorySize();
        validateNoOverlappingFeatures();
        validateNoDuplicateAdventurerName();
    }

    private void validateTerritorySize() {
        if (this.width <= 0 || this.height <= 0) {
            throw new IllegalArgumentException("Width and height must be greater than zero but were {%d, %d}"
                    .formatted(width, height));
        }
    }

    /**
     * TODO validate with PO: an adventurer cannot have its initial coordinates equal to one of the treasure.
     */
    private void validateNoOverlappingFeatures() {
        List<Coordinates> allFeaturesCoordinates = getAllFeaturesCoordinates();
        Map<Coordinates, Long> featuresCountByCoordinate = allFeaturesCoordinates.stream()
                .collect(Collectors.groupingBy(identity(),
                        Collectors.counting()));

        Map<Coordinates, Long> overlapsCountByCoordinate = MapStream.of(featuresCountByCoordinate)
                .filterValue(count -> count > 1)
                .toMap();

        if(!overlapsCountByCoordinate.isEmpty()){
            String overlapsString = overlapsCountByCoordinate.keySet().toString();
            String message = "Cannot build territory because of overlapping features at %s".formatted(overlapsString);
            throw new IllegalArgumentException(message);
        }
    }

    private void validateNoDuplicateAdventurerName() {
        Map<String, Long> adventurersCountByName = this.adventurers.stream()
                .collect(Collectors.groupingBy(Adventurer::getName, Collectors.counting()));

        Map<String, Long> duplicateAdventurersNames = MapStream.of(adventurersCountByName)
                .filterValue(count -> count > 1)
                .toMap();

        if(!duplicateAdventurersNames.isEmpty()){
            String duplicateAdventurersNamesString = duplicateAdventurersNames.keySet().toString();
            String message = "Cannot build territory because of duplicate adventurers names: %s".formatted(duplicateAdventurersNamesString);
            throw new IllegalArgumentException(message);
        }
    }

    private List<Coordinates> getAllFeaturesCoordinates() {
        List<Coordinates> treasuresCoordinates = this.treasures.stream().map(Treasure::coordinates).toList();
        List<Coordinates> mountainsCoordinates = this.mountains.stream().map(Mountain::coordinates).toList();
        List<Coordinates> adventurersCoordinates = this.getAdventurers().stream().map(Adventurer::getCoordinates).toList();
        List<Coordinates> allFeaturesCoordinates = new ArrayList<>();
        allFeaturesCoordinates.addAll(treasuresCoordinates);
        allFeaturesCoordinates.addAll(mountainsCoordinates);
        allFeaturesCoordinates.addAll(adventurersCoordinates);
        return allFeaturesCoordinates;
    }

}
