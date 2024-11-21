package org.example;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.IntegerArbitrary;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static net.jqwik.api.Arbitraries.integers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TreasureHuntingApplicationShould {
    @Property
    void create_territory_with_specified_size(@ForAll("validPairsOfWidthAndHeight") IntegerPair pair) {
        // GIVEN
        Integer width = pair.first();
        Integer height = pair.second();

        // WHEN
        Territory madreDeDios = new Territory(width,
                height,
                emptyList(),
                emptyList(),
                emptyList());

        // THEN
        assertThat(madreDeDios.getWidth()).isEqualTo(width);
        assertThat(madreDeDios.getHeight()).isEqualTo(height);
    }

    @Property
    void throw_exception_for_invalid_width_or_height(@ForAll("invalidPairsOfWidthAndHeight") IntegerPair pair) {
        // GIVEN
        Integer width = pair.first();
        Integer height = pair.second();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Territory(width,
                height,
                emptyList(),
                emptyList(),
                emptyList());

        // THEN
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Width and height must be greater than zero");
    }

    @Test
    void create_territory_with_mountains() {
        // GIVEN
        List<Mountain> mountains = List.of(
                getMountain(1, 1),
                getMountain(2, 2)
        );

        // WHEN
        Territory madreDeDios = new Territory(3,
                4,
                mountains,
                emptyList(),
                emptyList());

        // THEN
        assertThat(madreDeDios.getMountains())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(mountains);
    }

    /**
     * Assumption to validate with Product Owner: overlapping mountains are probably an error, and we would rather fail
     * fast than producing silent erroneous results. The PO might want the overlapping moutains to be ignored and
     * considered as one.
     */
    @Test
    void throw_exception_if_overlapping_mountains() {
        // GIVEN
        int westEastCoordinate = 1;
        int northSouthCoordinate = 1;
        List<Mountain> mountains = List.of(
                getMountain(westEastCoordinate, northSouthCoordinate),
                getMountain(1, 1)
        );

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Territory(3,
                4,
                mountains,
                emptyList(),
                emptyList());

        // THEN
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class)
                // TODO improve the message and print the coordinates of the overlapping mountains
                .hasMessage("Cannot build territory because of overlapping mountains.");
    }

    private static Mountain getMountain(int westEastCoordinate, int northSouthCoordinate) {
        return Mountain.plop(westEastCoordinate, northSouthCoordinate);
    }

    @Test
    void create_territory_with_treasures() {
        // GIVEN
        List<Treasure> treasures = List.of(
                new Treasure(new Coordinates(1, 1), 1),
                new Treasure(new Coordinates(2, 2), 2)
        );

        // WHEN
        Territory madreDeDios = new Territory(3,
                4,
                emptyList(),
                treasures,
                emptyList());

        // THEN
        assertThat(madreDeDios.getTreasures())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(treasures);
    }

    /**
     * Assumption to validate with Product Owner: overlapping treasure are considered as errors, and we would rather
     * fail fast. The PO might have another opinion on the matter, like adding the treasure or ignoring all except the
     * first or last one.
     */
    @Test
    void throw_exception_if_overlapping_treasures() {
        // GIVEN
        List<Treasure> treasures = List.of(
                new Treasure(new Coordinates(1, 2), 3),
                new Treasure(new Coordinates(1, 2), 2),
                new Treasure(new Coordinates(2, 2), 2)
        );

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Territory(3, 4, emptyList(), treasures, emptyList());

        // THEN
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class)
                // TODO improve the message and print the coordinates of the overlapping treasures
                .hasMessage("Cannot build territory because of overlapping treasures.");
    }

    @Test
    void create_territory_with_adventurers() {
        // GIVEN
        List<Adventurer> adventurers = List.of(
                new Adventurer("Adventurer #1",
                        new Coordinates(1, 1),
                        Orientation.NORTH,
                        0),
                new Adventurer("Adventurer #2",
                        new Coordinates(2, 2),
                        Orientation.NORTH,
                        0)
        );

        // WHEN
        Territory madreDeDios = new Territory(3,
                4,
                emptyList(),
                emptyList(),
                adventurers);

        // THEN
        assertThat(madreDeDios.getAdventurers())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(adventurers);
    }

    @Provide
    Arbitrary<IntegerPair> validPairsOfWidthAndHeight() {
        return Combinators.combine(integers(), integers())
                .filter((integer, integer2) -> integer > 0 && integer2 > 0)
                .as(IntegerPair::new);
    }

    @Provide
    Arbitrary<IntegerPair> invalidPairsOfWidthAndHeight() {
        return Combinators.combine(integers(), integers())
                .filter((integer, integer2) -> integer <= 0 || integer2 <= 0)
                .as(IntegerPair::new);
    }

    record IntegerPair(Integer first, Integer second) {
    }
}
