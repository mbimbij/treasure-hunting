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
import static org.junit.jupiter.api.Assertions.*;

class TerritoryShould {
    private static final Coordinates COORDINATES_1_1 = new Coordinates(1, 1);
    private static final Coordinates COORDINATES_1_2 = new Coordinates(1, 2);
    private static final Coordinates COORDINATES_1_3 = new Coordinates(1, 3);
    private static final Treasure TREASURE_AT_1_1 = new Treasure(COORDINATES_1_1, 3);
    private static final Mountain MOUNTAIN_AT_1_1 = new Mountain(COORDINATES_1_1);
    private static final Player PLAYER_1 = new Player("Player #1",
            COORDINATES_1_1,
            Orientation.NORTH,
            0);
    private static final Player PLAYER_2 = new Player("Player #2",
            COORDINATES_1_2,
            Orientation.NORTH,
            0);
    private static final Player PLAYER_3 = new Player("Player #3",
            COORDINATES_1_3,
            Orientation.NORTH,
            0);

    @Property
    void create_territory_with_specified_size(@ForAll("validPairsOfWidthAndHeight") IntegerPair widthHeightPair) {
        // GIVEN
        Integer width = widthHeightPair.first();
        Integer height = widthHeightPair.second();

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
                new Mountain(new Coordinates(0, 0)),
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
    void create_territory_with_players() {
        // GIVEN
        List<Player> players = of(
                PLAYER_1,
                PLAYER_2
        );

        // WHEN
        Territory madreDeDios = new Territory(3,
                4,
                emptyList(),
                emptyList(),
                players);

        // THEN
        assertThat(madreDeDios.getPlayers())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(players);
    }

    @Property
    void throw_exception_for_invalid_width_or_height(@ForAll("invalidPairsOfWidthAndHeight") IntegerPair pair) {
        // GIVEN
        Integer width = pair.first();
        Integer height = pair.second();
        String expectedErrorMessage = Territory.INVALID_TERRITORY_SIZE_ERROR_MESSAGE_FORMAT.formatted(width, height);

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Territory(width,
                height,
                emptyList(),
                emptyList(),
                emptyList());

        // THEN
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith(expectedErrorMessage);
    }

    // TODO Add more cases
    @ParameterizedTest
    @MethodSource
    void throw_exception_if_multiple_players_have_the_same_name(List<Player> players, List<String> duplicatePlayersNames) {
        // GIVEN
        String expectedErrorMessage = Territory.DUPLICATE_PLAYERS_NAMES_ERROR_MESSAGE_FORMAT
                .formatted(duplicatePlayersNames);

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Territory(3,
                4,
                emptyList(),
                emptyList(),
                players);

        // THEN
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedErrorMessage);
    }

    private static Stream<Arguments> throw_exception_if_multiple_players_have_the_same_name() {
        Arguments oneDuplicate = Arguments.of(of(PLAYER_1, PLAYER_1.withCoordinates(COORDINATES_1_2)), of(PLAYER_1.getName()));
        Arguments twoDuplicates = Arguments.of(of(
                        PLAYER_1.withCoordinates(new Coordinates(0, 0)),
                        PLAYER_1.withCoordinates(new Coordinates(0, 1)),
                        PLAYER_2.withCoordinates(new Coordinates(1, 0)),
                        PLAYER_2.withCoordinates(new Coordinates(1, 1)),
                        PLAYER_3.withCoordinates(new Coordinates(2, 0))
                ),
                of(PLAYER_1.getName(), PLAYER_2.getName()));

        Arguments[] arguments = {
                oneDuplicate,
                twoDuplicates,
        };

        return Stream.of(arguments);
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
     * Because JQwik tests are not executed in non-static inner classes, but junit @Nested test classes must be
     * non-static
     */
    @Nested
    class OverlappingFeatures {

        private static final List<Player> OVERLAPPING_ADVENTURERS = of(
                PLAYER_1,
                PLAYER_2.withCoordinates(PLAYER_1.getCoordinates())
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
         * @param players
         */
        @ParameterizedTest
        @MethodSource
        void throw_exception_if_overlapping_features(List<Mountain> mountains,
                                                     List<Treasure> treasures,
                                                     List<Player> players) {
            // GIVEN
            List<Coordinates> expectedOverlap = of(COORDINATES_1_1);
            String expectedErrorMessage = Territory.OVERLAPPING_FEATURES_ERROR_MESSAGE_FORMAT
                    .formatted(expectedOverlap);

            // WHEN
            ThrowableAssert.ThrowingCallable throwingCallable = () -> new Territory(3,
                    4,
                    mountains,
                    treasures,
                    players);

            // THEN
            assertThatThrownBy(throwingCallable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageStartingWith(expectedErrorMessage);
        }

        private static Stream<Arguments> throw_exception_if_overlapping_features() {
            return Stream.of(
                    Arguments.of(OVERLAPPING_MOUNTAINS, emptyList(), emptyList()),
                    Arguments.of(emptyList(), OVERLAPPING_TREASURES, emptyList()),
                    Arguments.of(emptyList(), emptyList(), OVERLAPPING_ADVENTURERS),
                    Arguments.of(of(MOUNTAIN_AT_1_1), of(TREASURE_AT_1_1), emptyList()),
                    Arguments.of(of(MOUNTAIN_AT_1_1), emptyList(), of(PLAYER_1)),
                    Arguments.of(emptyList(), of(TREASURE_AT_1_1), of(PLAYER_1)),
                    Arguments.of(of(MOUNTAIN_AT_1_1), of(TREASURE_AT_1_1), of(PLAYER_1))
            );
        }
    }

    @Nested
    class OutOfBoundFeatures {

        private static final int width = 3;
        private static final int height = 4;

        @ParameterizedTest
        @MethodSource
        void throw_exception_if_feature_out_of_bound(Integer width,
                                                     Integer height,
                                                     List<Mountain> mountains,
                                                     List<Coordinates> expectedInvalidCoordinates) {
            // GIVEN
            List<Treasure> treasures = emptyList();
            List<Player> players = emptyList();

            String expectedErrorMessage = Territory.FEATURES_COORDINATES_OUT_OF_BOUND_ERROR_MESSAGE
                    .formatted(expectedInvalidCoordinates);

            // WHEN
            ThrowableAssert.ThrowingCallable throwingCallable = () -> new Territory(width,
                    height,
                    mountains,
                    treasures,
                    players);

            // THEN
            assertThatThrownBy(throwingCallable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageStartingWith(expectedErrorMessage);
        }

        private static Stream<Arguments> throw_exception_if_feature_out_of_bound() {
            Arguments[] arguments = {
                    Arguments.of(width, height, of(new Mountain(tooMuchWest())), of(tooMuchWest())),
                    Arguments.of(width, height, of(new Mountain(tooMuchEast())), of(tooMuchEast())),
                    Arguments.of(width, height, of(new Mountain(tooMuchNorth())), of(tooMuchNorth())),
                    Arguments.of(width, height, of(new Mountain(tooMuchSouth())), of(tooMuchSouth())),
                    multipleMountainsOutOfBounds(),
            };
            return Stream.of(arguments);
        }

        private static Arguments multipleMountainsOutOfBounds() {
            return Arguments.of(width,
                    height,
                    of(MOUNTAIN_AT_1_1, new Mountain(tooMuchSouth()), new Mountain(tooMuchWest())),
                    of(tooMuchSouth(), tooMuchWest()));
        }

        private static Coordinates tooMuchSouth() {
            return new Coordinates(1, height + 2);
        }

        private static Coordinates tooMuchNorth() {
            return new Coordinates(1, -1);
        }

        private static Coordinates tooMuchEast() {
            return new Coordinates(width + 2, 2);
        }

        private static Coordinates tooMuchWest() {
            return new Coordinates(-1, 2);
        }
    }

    record IntegerPair(Integer first, Integer second) {
    }
}
