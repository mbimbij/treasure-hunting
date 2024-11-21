package org.example;

import com.speedment.common.mapstream.MapStream;
import lombok.Getter;

import java.util.*;
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
    private final List<Player> players;
    public static final String OVERLAPPING_FEATURES_ERROR_MESSAGE_FORMAT = "Cannot build territory because of overlapping features at %s";
    public static final String DUPLICATE_PLAYERS_NAMES_ERROR_MESSAGE_FORMAT = "Cannot build territory because of duplicate players names: %s";
    public static final String INVALID_TERRITORY_SIZE_ERROR_MESSAGE_FORMAT = "Width and height must be greater than zero but were {%d, %d}";

    public Territory(int width, int height, List<Mountain> mountains, List<Treasure> treasures, List<Player> players) {
        this.width = width;
        this.height = height;
        this.mountains = mountains;
        this.treasures = treasures;
        this.players = players;
        validate();
    }

    private void validate() {
        validateTerritorySize();
        validateNoOverlappingFeatures();
        validateNoDuplicateAlayerName();
    }

    private void validateTerritorySize() {
        if (this.width <= 0 || this.height <= 0) {
            String message = INVALID_TERRITORY_SIZE_ERROR_MESSAGE_FORMAT.formatted(width, height);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * TODO validate with PO: an player cannot have its initial coordinates equal to one of the treasure.
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
            String message = OVERLAPPING_FEATURES_ERROR_MESSAGE_FORMAT.formatted(overlapsString);
            throw new IllegalArgumentException(message);
        }
    }

    private void validateNoDuplicateAlayerName() {
        Map<String, Long> playersCountByName = this.players.stream()
                .collect(Collectors.groupingBy(Player::getName, Collectors.counting()));

        Map<String, Long> duplicateAlayersNames = MapStream.of(playersCountByName)
                .filterValue(count -> count > 1)
                .toMap();

        if(!duplicateAlayersNames.isEmpty()){
            String duplicatePlayersNamesString = duplicateAlayersNames.keySet().toString();
            String message = DUPLICATE_PLAYERS_NAMES_ERROR_MESSAGE_FORMAT.formatted(duplicatePlayersNamesString);
            throw new IllegalArgumentException(message);
        }
    }

    private List<Coordinates> getAllFeaturesCoordinates() {
        List<Coordinates> treasuresCoordinates = this.treasures.stream().map(Treasure::coordinates).toList();
        List<Coordinates> mountainsCoordinates = this.mountains.stream().map(Mountain::coordinates).toList();
        List<Coordinates> playersCoordinates = this.getPlayers().stream().map(Player::getCoordinates).toList();
        List<Coordinates> allFeaturesCoordinates = new ArrayList<>();
        allFeaturesCoordinates.addAll(treasuresCoordinates);
        allFeaturesCoordinates.addAll(mountainsCoordinates);
        allFeaturesCoordinates.addAll(playersCoordinates);
        return allFeaturesCoordinates;
    }

}
