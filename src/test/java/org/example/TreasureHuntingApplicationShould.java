package org.example;

import net.jqwik.api.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static net.jqwik.api.Arbitraries.integers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TreasureHuntingApplicationShould {

    private static final Coordinates COORDINATES_1_1 = new Coordinates(1, 1);
    private static final Treasure TREASURE_AT_1_1 = new Treasure(COORDINATES_1_1, 3);
    private static final Mountain MOUNTAIN_AT_1_1 = new Mountain(COORDINATES_1_1);
    private static final Coordinates COORDINATES_1_2 = new Coordinates(1, 2);
    private static final Adventurer ADVENTURER_1 = new Adventurer("Adventurer #1",
            COORDINATES_1_1,
            Orientation.NORTH,
            0);
    private static final Adventurer ADVENTURER_2 = new Adventurer("Adventurer #2",
            COORDINATES_1_2,
            Orientation.NORTH,
            0);
    private static final Coordinates COORDINATES_1_3 = new Coordinates(1, 3);

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

    @Test
    void create_territory_with_mountains() {
        // GIVEN
        List<Mountain> mountains = of(
                MOUNTAIN_AT_1_1,
                new Mountain(COORDINATES_1_2)
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

    @Test
    void create_territory_with_treasures() {
        // GIVEN
        List<Treasure> treasures = of(
                new Treasure(COORDINATES_1_1, 1),
                new Treasure(COORDINATES_1_2, 2)
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

    @Test
    void create_territory_with_adventurers() {
        // GIVEN
        List<Adventurer> adventurers = of(
                ADVENTURER_1,
                ADVENTURER_2
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
                .hasMessageStartingWith("Width and height must be greater than zero but were");
    }

    @Provide
    Arbitrary<IntegerPair> validPairsOfWidthAndHeight() {
        return Combinators.combine(integers(), integers())
                .filter((integer, integer2) -> integer > 0 && integer2 > 0)
                .as(IntegerPair::new);
    }

    @Provide
    private Arbitrary<IntegerPair> invalidPairsOfWidthAndHeight() {
        return Combinators.combine(integers(), integers())
                .filter((integer, integer2) -> integer <= 0 || integer2 <= 0)
                .as(IntegerPair::new);
    }

    /**
     * Because JQwik tests are not executed in non-static inner classes, but junit @Nested test classes must be non-static
     */
    @Nested
    class NonPBTErrorCases {

        private static final List<Adventurer> OVERLAPPING_ADVENTURERS = of(
                ADVENTURER_1,
                ADVENTURER_2.withCoordinates(ADVENTURER_1.getCoordinates())
        );
        private static final List<Mountain> OVERLAPPING_MOUNTAINS = of(
                MOUNTAIN_AT_1_1,
                MOUNTAIN_AT_1_1,
                new Mountain(COORDINATES_1_2)
        );
        private static final List<Treasure> OVERLAPPING_TREASURES = of(
                TREASURE_AT_1_1,
                new Treasure(COORDINATES_1_1, 2),
                new Treasure(COORDINATES_1_2, 2)
        );

        /**
         * TODO validate with PO: overlapping mountains are probably an error, and we would rather fail
         * fast than producing silent erroneous results. The PO might want the overlapping mountains to be ignored and
         * considered as one.
         * TODO validate with PO: overlapping treasure are considered as errors, and we would rather
         * fail fast. The PO might have another opinion on the matter, like adding the treasure or ignoring all except the
         * first or last one.
         *
         * @param mountains
         * @param treasures
         * @param adventurers
         */
        @ParameterizedTest
        @MethodSource
        void throw_exception_if_overlapping_features(List<Mountain> mountains,
                                                     List<Treasure> treasures,
                                                     List<Adventurer> adventurers) {
            // WHEN
            ThrowableAssert.ThrowingCallable throwingCallable = () -> new Territory(3,
                    4,
                    mountains,
                    treasures,
                    adventurers);

            // THEN
            assertThatThrownBy(throwingCallable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageStartingWith("Cannot build territory because of overlapping features at");
        }

        @Test
        void throw_exception_if_2_adventurers_have_same_name() {
            // GIVEN
            List<Adventurer> adventurers = of(
                    ADVENTURER_1,
                    ADVENTURER_2.withName(ADVENTURER_1.getName())
            );

            // WHEN
            ThrowableAssert.ThrowingCallable throwingCallable = () -> new Territory(3,
                    4,
                    emptyList(),
                    emptyList(),
                    adventurers);

            // THEN
            assertThatThrownBy(throwingCallable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cannot build territory because of duplicate adventurers names: [%s]".formatted(ADVENTURER_1.getName()));
        }

        private static Stream<Arguments> throw_exception_if_overlapping_features() {
            return Stream.of(
                    Arguments.of(OVERLAPPING_MOUNTAINS, emptyList(), emptyList()),
                    Arguments.of(emptyList(), OVERLAPPING_TREASURES, emptyList()),
                    Arguments.of(emptyList(), emptyList(), OVERLAPPING_ADVENTURERS),
                    Arguments.of(of(MOUNTAIN_AT_1_1), of(TREASURE_AT_1_1), emptyList()),
                    Arguments.of(of(MOUNTAIN_AT_1_1), emptyList(), of(ADVENTURER_1)),
                    Arguments.of(emptyList(), of(TREASURE_AT_1_1), of(ADVENTURER_1)),
                    Arguments.of(of(MOUNTAIN_AT_1_1), of(TREASURE_AT_1_1), of(ADVENTURER_1))
            );
        }
    }

    record IntegerPair(Integer first, Integer second) {
    }
}
