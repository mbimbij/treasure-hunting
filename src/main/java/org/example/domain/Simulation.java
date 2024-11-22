package org.example.domain;

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
 * Represent 'Madre de Dios' simulation. I opted for a noun for the class name and 'Madre de Dios' for the instance.
 * name. "Carte" translates to "Map", and I didn't want to have potential collisions with java.util.Map, so I decided on
 * "Territory".
 * ETA: switched to "Simulation"
 */
@Getter
public class Simulation {
    public static final String OVERLAPPING_FEATURES_ERROR_MESSAGE_FORMAT = "Cannot build simulation because of overlapping features at %s";
    public static final String DUPLICATE_PLAYERS_NAMES_ERROR_MESSAGE_FORMAT = "Cannot build simulation because of duplicate players names: %s";
    public static final String INVALID_SIMULATION_SIZE_ERROR_MESSAGE_FORMAT = "Width and height must be greater than zero but were {%d, %d}";
    public static final String FEATURES_COORDINATES_OUT_OF_BOUND_ERROR_MESSAGE = "Some features are located outside the simulation: %s";

    // TODO question au PO: min et max pour la largeur et la hauteur de la carte ? Si oui, amha, la validation devrait être sortie de la classe Simulation, mais il y a de bons arguments pour le contraire.
    private final Size size;
    // TODO question au PO: min montagnes: 0  1 ? max montagnes: un nombre constant, en entrée de l'application, ou calculé en fonction de la taille de la carte et du nombre de features ?
    private final List<Mountain> mountains;
    // TODO question au PO: min trésors: 0 ou 1 ? max trésors: un nombre constant, en entrée de l'application, ou calculé en
    private final List<Treasure> treasures;
    // TODO question au PO: min joueur: 0 ou 1 ? max joueurs: un nombre constant, en entrée de l'application, ou calculé en
    private final List<Player> players;

    public Simulation(Size size, List<Mountain> mountains, List<Treasure> treasures, List<Player> players) {
        this.size = size;
        this.mountains = mountains;
        this.treasures = treasures;
        this.players = players;
        validate();
    }

    public int getWidth() {
        return size.width();
    }

    public int getHeight() {
        return size.height();
    }

    public void run() {
        while (commandsRemaining()) {
            playTurn();
        }
    }

    public void playTurn() {
        for (Player player : players) {
            player.pollNextCommand().ifPresent(command -> {
                switch (command) {
                    case A -> moveForward(player);
                    case G -> turnLeft(player);
                    case D -> turnRight(player);
                }
            });
        }
    }

    /**
     * At the moment, as the application is simple enough, and for early stages of the project, i decided to place the
     * have the simulation validate itself at construction time for the sake of simplicity and not over-engineer things.
     * Also early on, data and validation itself was pretty cohesive
     * <p>
     * As the validation logic and the class itself grows, it might be appropriate to put it in either a Factory or a
     * Validator, in order to 1) Prevent bloating, 2) enforce separation of concerns between validating simulation
     * parameters and running the simulation itself.
     * <p>
     * Especially considering the possibility of validating min and max values for width, height, number of mountains,
     * treasures, players. Or considering even more complex validation logic and creation logic, like procedural
     * generation.
     * <p>
     * ETA: At that point the validation is large enough, and it is 100% valid to consider it a separate concern or
     * "responsibility" / "reason to change". If time allows, it will be extracted
     */
    private void validate() {
        validateSimulationSize();
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
               || coordinates.westEast() >= size.width()
               || coordinates.northSouth() < 0
               || coordinates.northSouth() >= size.height();
    }

    private void validateSimulationSize() {
        if (this.size.width() <= 0 || this.size.height() <= 0) {
            String message = INVALID_SIMULATION_SIZE_ERROR_MESSAGE_FORMAT.formatted(this.size.width(), this.size.height());
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

    public record Size(Integer width, Integer height) {
    }
}
