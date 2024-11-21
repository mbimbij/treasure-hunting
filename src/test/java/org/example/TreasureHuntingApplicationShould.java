package org.example;

import net.jqwik.api.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static net.jqwik.api.Arbitraries.integers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TreasureHuntingApplicationShould {

    private final Coordinates coordinates_1_1 = new Coordinates(1, 1);
    private final Coordinates coordinates_1_2 = new Coordinates(1, 2);

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
                new Mountain(coordinates_1_1),
                new Mountain(coordinates_1_2)
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
        List<Mountain> mountains = List.of(
                new Mountain(coordinates_1_1),
                new Mountain(coordinates_1_1)
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

    @Test
    void create_territory_with_treasures() {
        // GIVEN
        List<Treasure> treasures = List.of(
                new Treasure(coordinates_1_1, 1),
                new Treasure(coordinates_1_2, 2)
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
                new Treasure(coordinates_1_1, 3),
                new Treasure(coordinates_1_1, 2),
                new Treasure(coordinates_1_2, 2)
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
                        coordinates_1_1,
                        Orientation.NORTH,
                        0),
                new Adventurer("Adventurer #2",
                        coordinates_1_2,
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
