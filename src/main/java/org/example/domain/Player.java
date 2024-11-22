package org.example.domain;

import lombok.*;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Optional;
import java.util.Queue;

/**
 * Not making it a record, as the treasure count will be mutable, and returning a new instance with changed state feels
 * like overkill at the moment. And thinking in DDD terms, Adventurer feels like an entity rather than a value object,
 * which feels less appropriated as a record.
 * <p>
 * ETA: Also, taking a page from DDD's book, the name will be used as the "id" of the entity, and will be used as the
 * identity of the entity and to detect duplicates
 * <p>
 * ETA2: Following "strict" TDD, i should have postponed the introduction of "coordinates", "orientation" and
 * "treasuresCount", but as we know for sure they are in the requirements, it seems appropriate enough to me to do
 * introduce them before the tests. It does not appear as meaningless future-proofing.
 * <p>
 * TODO à confirmer avec le PO: je pars du principe qu'il est ok que les joueurs n'aient pas une liste de commandes de taille égale et aucune validation n'est exécutée dessus. Vous confirmez ?
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@With
@ToString(includeFieldNames = false)
public class Player implements CanCollideWith {
    @EqualsAndHashCode.Include
    private final String name;
    private Coordinates coordinates;
    private Orientation orientation;
    private int collectedTreasuresCount;
    private Queue<Command> remainingCommands;

    public Player(String name, Coordinates coordinates, Orientation orientation) {
        this(name, coordinates, orientation, new ArrayDeque<>());
    }

    public Player(String name, Coordinates coordinates, Orientation orientation, Collection<Command> remainingCommands) {
        this.orientation = orientation;
        this.name = name;
        this.coordinates = coordinates;
        this.collectedTreasuresCount = 0;
        this.remainingCommands = new ArrayDeque<>(remainingCommands);
    }

    public Player(String name, Coordinates coordinates, Orientation orientation, int collectedTreasuresCount, Collection<Command> remainingCommands) {
        this.name = name;
        this.coordinates = coordinates;
        this.orientation = orientation;
        this.collectedTreasuresCount = collectedTreasuresCount;
        this.remainingCommands = new ArrayDeque<>(remainingCommands);
    }

    public void moveForward() {
        switch (getOrientation()) {
            case NORTH -> coordinates = coordinates.northOf();
            case EAST -> coordinates = coordinates.eastOf();
            case SOUTH -> coordinates = coordinates.southOf();
            case WEST -> coordinates = coordinates.westOf();
        }
    }


    Coordinates getFuturePosition() {
        return switch (getOrientation()) {
            case NORTH -> coordinates.northOf();
            case EAST -> coordinates.eastOf();
            case SOUTH -> coordinates.southOf();
            case WEST -> coordinates.westOf();
        };
    }

    @Override
    public Coordinates coordinates() {
        return getCoordinates();
    }

    public void turnLeft() {
        this.orientation = this.orientation.leftOf();
    }

    public void turnRight() {
        this.orientation = this.orientation.rightOf();
    }

    public void collectTreasure() {
        this.collectedTreasuresCount++;
    }

    public Optional<Command> popNextCommand() {
        return Optional.ofNullable(this.remainingCommands.poll());
    }

    boolean hasRemainingCommands() {
        return !getRemainingCommands().isEmpty();
    }
}