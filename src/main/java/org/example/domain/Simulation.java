package org.example.domain;

import lombok.Getter;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

/**
 * Represent 'Madre de Dios' simulation. I opted for a noun for the class name and 'Madre de Dios' for the instance.
 * name. "Carte" translates to "Map", and I didn't want to have potential collisions with java.util.Map, so I decided on
 * "Territory".
 * <p>
 * ETA: Changed name to "Simulation"
 * <p>
 * ETA2: class is not a record, despite IDE warning, for the same reason as Player. Its internal state "mutates", due to
 * players and treasures changing states. And thinking in DDD terms, it would align with an entity, an aggregate even,
 * and a record for an aggregate doesn't feel right.
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
        new SimulationValidator().validate(this);
    }

    public void run() {
        while (areCommandsRemaining()) {
            playTurn();
        }
    }

    void playTurn() {
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
                .filter(treasure ->
                        treasure.intersectsWith(playerCoordinates)
                        && !treasure.isEmpty())
                .findAny();
    }

    private boolean collidesWithMountain(Coordinates futurePosition) {
        return this.mountains
                .stream()
                .anyMatch(mountain -> mountain.intersectsWith(futurePosition));
    }

    private boolean collidesWithAnotherPlayer(Coordinates futurePosition) {
        return this.players
                .stream()
                .anyMatch(otherPlayer -> otherPlayer.intersectsWith(futurePosition));
    }

    void turnLeft(Player player) {
        player.turnLeft();
    }

    void turnRight(Player player) {
        player.turnRight();
    }

    boolean areCommandsRemaining() {
        return players.stream().anyMatch(Player::hasRemainingCommands);
    }

    /**
     * Decided on moving that method back to Territory class as otherwise it felt like the logic of the territory was
     * leaking into the Coordinates class.
     *
     * @param coordinates
     * @return
     */
    public boolean isOutOfBound(Coordinates coordinates) {
        return coordinates.westEast() < 0
               || coordinates.westEast() >= size.width()
               || coordinates.northSouth() < 0
               || coordinates.northSouth() >= size.height();
    }

    public record Size(Integer width, Integer height) {
    }

}
