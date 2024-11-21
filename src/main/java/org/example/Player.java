package org.example;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

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
 *
 * TODO à confirmer avec le PO: je pars du principe qu'il est ok que les joueurs n'aient pas une liste de commandes de taille égale et aucune validation n'est exécutée dessus. Vous confirmez ?
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@With
@ToString(includeFieldNames = false)
public class Player {
    @EqualsAndHashCode.Include
    private final String name;
    private final Coordinates coordinates;
    private final Orientation orientation;
    private final int treasuresCount;

    public Player(String name, Coordinates coordinates, Orientation orientation, int treasuresCount) {
        this.orientation = orientation;
        this.name = name;
        this.coordinates = coordinates;
        this.treasuresCount = treasuresCount;
    }

    public Player(String name, Coordinates coordinates, Orientation orientation) {
        this.orientation = orientation;
        this.name = name;
        this.coordinates = coordinates;
        this.treasuresCount = 0;
    }
}
