package org.example;

import com.speedment.common.mapstream.MapStream;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Represent 'Madre de Dios' territory. I opted for a noun for the class name and 'Madre de Dios' for the instance.
 * name. "Carte" translates to "Map", and I didn't want to have potential collisions with java.util.Map, so I decided on
 * "Territory".
 */
@Getter
public class Territory {
    public static final String OVERLAPPING_FEATURES_ERROR_MESSAGE_FORMAT = "Cannot build territory because of overlapping features at %s";
    public static final String DUPLICATE_PLAYERS_NAMES_ERROR_MESSAGE_FORMAT = "Cannot build territory because of duplicate players names: %s";
    public static final String INVALID_TERRITORY_SIZE_ERROR_MESSAGE_FORMAT = "Width and height must be greater than zero but were {%d, %d}";
    public static final String FEATURES_COORDINATES_OUT_OF_BOUND_ERROR_MESSAGE = "Some features are located outside the territory: %s";

    // TODO question au PO: min et max pour la largeur et la hauteur de la carte ? Si oui, amha, la validation devrait être sortie de la classe Territory, mais il y a de bons arguments pour le contraire.
    private final int width;
    private final int height;
    // TODO question au PO: min montagnes: 0  1 ? max montagnes: un nombre constant, en entrée de l'application, ou calculé en fonction de la taille de la carte et du nombre de features ?
    private final List<Mountain> mountains;
    // TODO question au PO: min trésors: 0 ou 1 ? max trésors: un nombre constant, en entrée de l'application, ou calculé en
    private final List<Treasure> treasures;
    // TODO question au PO: min joueur: 0 ou 1 ? max joueurs: un nombre constant, en entrée de l'application, ou calculé en
    private final List<Player> players;

    /**
     * // TODO Introduce a builder maybe ? The desire to use the same Territory in multiple tests cases calls for some
     * sort of builder, and calling the validation logic on "build()" or some "startSimulation() method"
     *
     * @param width
     * @param height
     * @param mountains
     * @param treasures
     * @param players
     */
    public Territory(int width, int height, List<Mountain> mountains, List<Treasure> treasures, List<Player> players) {
        this.width = width;
        this.height = height;
        this.mountains = mountains;
        this.treasures = treasures;
        this.players = players;
        validate();
    }

    public void runSimulation() {
        while (commandsRemaining()) {
            playTurn();
        }
    }

    public void playTurn() {
        for (Player player : players) {
            player.popNextCommand().ifPresent(command -> {
                switch (command) {
                    case A -> moveForward(player);
                    case G -> turnLeft(player);
                    case D -> turnRight(player);
                }
                ;
            });
        }
    }

    /**
     * As the validation logic grows, it might be appropriate to put it in either a factory or a Validator. Especially
     * considering the possibility of validating min and max values for width, height, number of mountains, treasures,
     * players. Or considering even more complex validation logic and creation logic, like procedural generation.
     */
    private void validate() {
        validateTerritorySize();
        validateNoOverlappingFeatures();
        validateNoFeatureOutOfBound();
        validateNoDuplicatePlayerName();
    }

    private void validateNoFeatureOutOfBound() {
        List<Coordinates> allFeaturesCoordinates = getAllFeaturesCoordinates();
        Map<Coordinates, Long> outOfBoundFeatureCoordinates = allFeaturesCoordinates.stream()
                .filter(this::isOutOfBound)
                .collect(groupingBy(identity(), counting()));
        if (!outOfBoundFeatureCoordinates.isEmpty()) {
            String message = FEATURES_COORDINATES_OUT_OF_BOUND_ERROR_MESSAGE
                    .formatted(outOfBoundFeatureCoordinates.keySet());
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Decided on moving that method back to Territory class as otherwise it felt like the logic of the territory was
     * leaking into the Coordinates class.
     *
     * @param coordinates
     * @return
     */
    private boolean isOutOfBound(Coordinates coordinates) {
        return coordinates.westEast() < 0
               || coordinates.westEast() >= width
               || coordinates.northSouth() < 0
               || coordinates.northSouth() >= height;
    }

    private void validateTerritorySize() {
        if (this.width <= 0 || this.height <= 0) {
            String message = INVALID_TERRITORY_SIZE_ERROR_MESSAGE_FORMAT.formatted(width, height);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * TODO validate with PO: an player cannot have its initial coordinates equal to one of the treasure.
     * TODO explicit what features overlap to help with debugging (mountain, treasure, and later what line in the input file)
     */
    private void validateNoOverlappingFeatures() {
        List<Coordinates> allFeaturesCoordinates = getAllFeaturesCoordinates();
        Map<Coordinates, Long> featuresCountByCoordinate = allFeaturesCoordinates.stream()
                .collect(groupingBy(identity(),
                        counting()));

        Map<Coordinates, Long> overlapsCountByCoordinate = MapStream.of(featuresCountByCoordinate)
                .filterValue(count -> count > 1)
                .toMap();

        if (!overlapsCountByCoordinate.isEmpty()) {
            String overlapsString = overlapsCountByCoordinate.keySet().toString();
            String message = OVERLAPPING_FEATURES_ERROR_MESSAGE_FORMAT.formatted(overlapsString);
            throw new IllegalArgumentException(message);
        }
    }

    private void validateNoDuplicatePlayerName() {
        Map<String, Long> playersCountByName = this.players.stream()
                .collect(groupingBy(Player::getName, counting()));

        Map<String, Long> duplicateAlayersNames = MapStream.of(playersCountByName)
                .filterValue(count -> count > 1)
                .toMap();

        if (!duplicateAlayersNames.isEmpty()) {
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

    void moveForward(Player player) {
        Coordinates futurePosition = player.getFuturePosition();
        if (this.isOutOfBound(futurePosition)
            || this.collidesWithMountain(futurePosition)
            || this.collidesWithAnotherPlayer(futurePosition)) {
            return;
        }
        player.moveForward();
        collectTreasureIfApplicable(player);
    }

    private void collectTreasureIfApplicable(Player player) {
        Optional<Treasure> nonEmptyTreasure = getNonEmptyTreasureAtPlayersPosition(player.getCoordinates());
        nonEmptyTreasure.ifPresent(t -> {
            t.collectTreasure();
            player.collectTreasure();
        });
    }

    private Optional<Treasure> getNonEmptyTreasureAtPlayersPosition(Coordinates playerCoordinates) {
        return this.treasures
                .stream()
                .filter(treasure -> treasure.collidesWith(playerCoordinates))
                .findAny()
                .filter(treasure -> !treasure.isEmpty());
    }

    private boolean collidesWithMountain(Coordinates futurePosition) {
        return this.mountains
                .stream()
                .anyMatch(mountain -> mountain.collidesWith(futurePosition));
    }

    private boolean collidesWithAnotherPlayer(Coordinates futurePosition) {
        return this.players
                .stream()
                .anyMatch(otherPlayer -> otherPlayer.collidesWith(futurePosition));
    }

    public void turnLeft(Player player) {
        player.turnLeft();
    }

    public void turnRight(Player player) {
        player.turnRight();
    }

    private boolean commandsRemaining() {
        return players.stream().anyMatch(Player::hasRemainingCommands);
    }

}
