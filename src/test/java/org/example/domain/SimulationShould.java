package org.example.domain;

import net.jqwik.api.*;
import org.assertj.core.api.ThrowableAssert;
import org.example.TestDataFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static net.jqwik.api.Arbitraries.integers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.domain.Command.*;
import static org.example.domain.Orientation.*;
import static org.mockito.Mockito.*;

class SimulationShould {

    private static final Treasure TREASURE_AT_1_1 = new Treasure(1, 1, 3);

    private static final Player PLAYER_1 = new Player("Player #1",
            new Coordinates(1, 1),
            NORTH);
    private static final Player PLAYER_2 = new Player("Player #2",
            new Coordinates(1, 2),
            NORTH);
    private static final Player PLAYER_3 = new Player("Player #3",
            new Coordinates(1, 3),
            NORTH);

    @Property
    void create_simulation_with_specified_size(@ForAll("validPairsOfWidthAndHeight") Simulation.Size size) {
        // WHEN
        Simulation madreDeDios = Simulation.builder().withSize(size).build();

        // THEN
        assertThat(madreDeDios.getSize()).isEqualTo(size);
    }

    @Test
    void create_simulation_with_mountains() {
        // GIVEN
        List<Mountain> mountains = of(
                new Mountain(0, 0),
                new Mountain(1, 1),
                new Mountain(1, 2)
        );

        // WHEN
        Simulation madreDeDios = Simulation.builder()
                .withSize(3, 4)
                .withMountains(mountains)
                .build();

        // THEN
        assertThat(madreDeDios.getMountains())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(mountains);
    }

    @Test
    void create_simulation_with_treasures() {
        // GIVEN
        List<Treasure> treasures = of(
                new Treasure(new Coordinates(1, 1).westEast(), new Coordinates(1, 1).northSouth(), 1),
                new Treasure(new Coordinates(1, 2).westEast(), new Coordinates(1, 2).northSouth(), 2)
        );

        // WHEN
        Simulation madreDeDios = Simulation.builder()
                .withSize(3, 4)
                .withTreasures(treasures)
                .build();

        // THEN
        List<Treasure> expected = of(
                new Treasure(new Coordinates(1, 1).westEast(), new Coordinates(1, 1).northSouth(), 1),
                new Treasure(new Coordinates(1, 2).westEast(), new Coordinates(1, 2).northSouth(), 2)
        );
        assertThat(madreDeDios.getTreasures())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expected);
    }

    @Test
    void create_simulation_with_players() {
        // GIVEN
        List<Player> players = of(PLAYER_1, PLAYER_2);

        // WHEN
        Simulation madreDeDios = Simulation.builder()
                .withSize(3, 4)
                .withPlayers(players)
                .build();

        // THEN
        List<Player> expected = of(PLAYER_1, PLAYER_2);
        assertThat(madreDeDios.getPlayers())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expected);
    }

    @Property
    void throw_exception_for_invalid_width_or_height(@ForAll("invalidPairsOfWidthAndHeight") Simulation.Size size) {
        // GIVEN
        String expectedErrorMessage = Simulation.INVALID_SIMULATION_SIZE_ERROR_MESSAGE_FORMAT
                .formatted(size.width(), size.height());

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Simulation.builder()
                .withSize(size.width(), size.height())
                .build();

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
        String expectedErrorMessage = Simulation.DUPLICATE_PLAYERS_NAMES_ERROR_MESSAGE_FORMAT
                .formatted(duplicatePlayersNames);

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Simulation.builder()
                .withSize(3, 4)
                .withPlayers(players)
                .build();

        // THEN
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedErrorMessage);
    }

    /**
     * Uses a map similar to the one described in the instructions <br/>
     * <table border="1">
     * <tr><td>.</td><td>M</td><td>.</td></tr>
     * <tr><td>.</td><td>.</td><td>M</td></tr>
     * <tr><td>.</td><td>M</td><td>A(P2)</td></tr>
     * <tr><td>T(2)</td><td>T(3)</td><td>.</td></tr>
     * </table>
     * <br/>
     * Or, in non formatted javadoc:
     * .     M     .
     * .     .     M
     * .     M     A(P2)
     * T(2)  T(3)  .
     *
     * @param player1
     * @param expectedCoordinates
     */
    @ParameterizedTest
    @MethodSource
    void should_move_player_forward_respecting_boundaries_and_collisions(Player player1, Coordinates expectedCoordinates) {
        // GIVEN
        int width = 3;
        int height = 4;
        Player player2 = new Player("p2", new Coordinates(2, 2), NORTH);
        List<Player> players = of(player1, player2);
        List<Mountain> mountains = of(new Mountain(1, 0),
                new Mountain(2, 1),
                new Mountain(1, 2)
        );
        List<Treasure> treasures = of(
                new Treasure(0, 3, 2),
                new Treasure(1, 3, 3)
        );
        Simulation.Size size = new Simulation.Size(width, height);
        Simulation simulation = Simulation.builder()
                .withSize(size)
                .withMountains(mountains)
                .withTreasures(treasures)
                .withPlayers(players)
                .build();

        // WHEN
        simulation.moveForward(player1);

        // THEN
        assertThat(player1.getCoordinates()).isEqualTo(expectedCoordinates);
    }

    @Test
    void turn_player_left_and_right() {
        // GIVEN
        int width = 1;
        int height = 1;
        Player player = spy(new Player("player", new Coordinates(0, 0), NORTH));
        Simulation.Size size = new Simulation.Size(width, height);
        Simulation simulation = Simulation.builder()
                .withSize(size)
                .withPlayers(of(player))
                .build();

        // WHEN
        simulation.turnLeft(player);
        verify(player).turnLeft();

        // AND
        simulation.turnRight(player);
        verify(player).turnRight();
    }

    /**
     * Uses a map similar to the one described in the instructions <br/>
     * <table border="1">
     * <tr><td>.</td><td>T(3)</td><td>T(0)</td><td>T(7)</td></tr>
     * </table>
     * <br/>
     * Or, in non formatted javadoc:
     * .    T(3)  T(0)  T(7)
     */
    @Test
    void collect_treasure_iff_moving_on_it_and_non_empty() {
        // GIVEN
        int width = 4;
        int height = 1;
        Player player = new Player("player", new Coordinates(0, 0), EAST);
        List<Treasure> treasures = of(
                // Will be collected twice
                new Treasure(1, 0, 3),
                // Won't be collected
                new Treasure(2, 0, 0),
                // Will be collected once
                new Treasure(3, 0, 7)
        );
        Simulation.Size size = new Simulation.Size(width, height);
        // Will be collected twice
        // Won't be collected
        // Will be collected once
        Simulation simulation = Simulation.builder()
                .withSize(size)
                .withTreasures(treasures)
                .withPlayers(of(player))
                .build();

        // WHEN
        simulation.moveForward(player);
        simulation.moveForward(player);
        simulation.moveForward(player);
        simulation.turnRight(player);
        simulation.turnRight(player);
        simulation.moveForward(player);
        simulation.moveForward(player);
        simulation.moveForward(player);

        // THEN
        assertThat(player.getCollectedTreasuresCount()).isEqualTo(3);
        assertThat(simulation.getTreasures()).extracting(Treasure::quantity).isEqualTo(of(1, 0, 6));
    }

    @Test
    void execute_sequence_of_commands_on_single_player() {
        // GIVEN
        int width = 2;
        int height = 1;

        List<Command> commands = of(A, G, D, D, G);
        Player player = new Player("player", new Coordinates(0, 0), NORTH, commands);
        Simulation simulation = spy(Simulation.builder()
                .withSize(width, height)
                .withPlayers(of(player))
                .build());

        // WHEN
        simulation.run();

        // THEN
        InOrder inOrder = inOrder(simulation);
        inOrder.verify(simulation).moveForward(player);
        inOrder.verify(simulation).turnLeft(player);
        inOrder.verify(simulation, times(2)).turnRight(player);
        inOrder.verify(simulation).turnLeft(player);
    }

    @Test
    void execute_sequence_of_commands_on_multiple_players() {
        // GIVEN
        int width = 3;
        int height = 2;

        Player player1 = new Player("player", new Coordinates(0, 0), EAST, of(A, G, D, D, G));
        Player player2 = new Player("player2", new Coordinates(2, 1), WEST, of(D, A, G));
        Simulation.Size size = new Simulation.Size(width, height);
        Simulation simulation = spy(Simulation.builder()
                .withSize(width, height)
                .withPlayers(of(player1, player2))
                .build());

        // WHEN
        simulation.run();

        // THEN
        InOrder inOrder = inOrder(simulation);
        inOrder.verify(simulation).moveForward(player1);
        inOrder.verify(simulation).turnRight(player2);
        inOrder.verify(simulation).turnLeft(player1);
        inOrder.verify(simulation).moveForward(player2);
        inOrder.verify(simulation).turnRight(player1);
        inOrder.verify(simulation).turnLeft(player2);
        inOrder.verify(simulation).turnRight(player1);
        inOrder.verify(simulation).turnLeft(player1);
    }

    @Test
    void run_the_example_simulation_of_the_instructions() {
        // GIVEN
        int width = 3;
        int height = 4;

        List<Command> commands = of(A, A, D, A, D, A, G, G, A);
        Player playerBeforeSimulation = new Player("Lara",
                new Coordinates(1, 1),
                SOUTH,
                commands);
        Player expectedPlayerAfterSimulation = new Player("Lara",
                new Coordinates(0, 3),
                SOUTH,
                3,
                emptyList());
        List<Mountain> mountains = TestDataFactory.defaultMountains();
        List<Treasure> treasures = TestDataFactory.defaultTreasures();
        Simulation.Size size = new Simulation.Size(width, height);
        Simulation simulation = Simulation.builder()
                .withSize(size)
                .withMountains(mountains)
                .withTreasures(treasures)
                .withPlayers(of(playerBeforeSimulation))
                .build();

        // WHEN
        simulation.run();

        // THEN
        assertThat(simulation.getPlayers())
                .singleElement()
                .satisfies(l -> {
                    assertThat(l).usingRecursiveComparison()
                            .isEqualTo(expectedPlayerAfterSimulation);
                });
    }

    private static Stream<Arguments> should_move_player_forward_respecting_boundaries_and_collisions() {
        Player facingNoObstacle = new Player("player1", new Coordinates(0, 1), NORTH);
        Player facingNorthernLimit = new Player("player1", new Coordinates(0, 0), NORTH);
        Player facingEasternLimit = new Player("player1", new Coordinates(2, 3), EAST);
        Player facingSouthernLimit = new Player("player1", new Coordinates(2, 3), SOUTH);
        Player facingWesternLimit = new Player("player1", new Coordinates(0, 2), WEST);
        Player facingMountain = new Player("player", new Coordinates(1, 1), NORTH);
        Player facingOtherPlayer = new Player("player", new Coordinates(2, 3), NORTH);
        Player facingTreasure = new Player("player", new Coordinates(2, 3), WEST);
        Arguments[] arguments = new Arguments[]{
                Arguments.of(facingNoObstacle, new Coordinates(0, 0)),
                Arguments.of(facingNorthernLimit, new Coordinates(0, 0)),
                Arguments.of(facingEasternLimit, new Coordinates(2, 3)),
                Arguments.of(facingSouthernLimit, new Coordinates(2, 3)),
                Arguments.of(facingWesternLimit, new Coordinates(0, 2)),
                Arguments.of(facingMountain, new Coordinates(1, 1)),
                Arguments.of(facingOtherPlayer, new Coordinates(2, 3)),
                Arguments.of(facingTreasure, new Coordinates(1, 3))
        };
        return Stream.of(arguments);
    }

    private static Stream<Arguments> throw_exception_if_multiple_players_have_the_same_name() {
        Arguments oneDuplicate = Arguments.of(of(PLAYER_1, PLAYER_1.withCoordinates(new Coordinates(1, 2))), of(PLAYER_1.getName()));
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
    Arbitrary<Simulation.Size> validPairsOfWidthAndHeight() {
        return Combinators.combine(integers(), integers())
                .filter((integer, integer2) -> integer > 0 && integer2 > 0)
                .as(Simulation.Size::new);
    }

    @Provide
    private Arbitrary<Simulation.Size> invalidPairsOfWidthAndHeight() {
        return Combinators.combine(integers(), integers())
                .filter((integer, integer2) -> integer <= 0 || integer2 <= 0)
                .as(Simulation.Size::new);
    }

    /**
     * Because JQwik tests are not executed in non-static inner classes, but junit @Nested test classes must be
     * non-static
     */
    @Nested
    class OverlappingFeatures {
        private static final Mountain MOUNTAIN_AT_1_1 = new Mountain(1, 1);

        private static final List<Player> OVERLAPPING_ADVENTURERS = of(
                PLAYER_1,
                PLAYER_2.withCoordinates(PLAYER_1.getCoordinates())
        );
        private static final List<Mountain> OVERLAPPING_MOUNTAINS = of(
                MOUNTAIN_AT_1_1,
                MOUNTAIN_AT_1_1,
                new Mountain(new Coordinates(1, 2))
        );

        private static final List<Treasure> OVERLAPPING_TREASURES = of(
                TREASURE_AT_1_1,
                new Treasure(1, 1, 2),
                new Treasure(1, 2, 2)
        );

        /**
         * TODO validate with PO: overlapping mountains are probably an error, and we would rather fail
         * fast than producing silent erroneous results. The PO might want the overlapping mountains to be ignored and
         * considered as one.
         * TODO validate with PO: overlapping treasure are considered as errors, and we would rather
         * fail fast. The PO might have another opinion on the matter, like adding the treasure or ignoring all except the
         * width or last one.
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
            List<Coordinates> expectedOverlap = of(new Coordinates(1, 1));
            String expectedErrorMessage = Simulation.OVERLAPPING_FEATURES_ERROR_MESSAGE_FORMAT
                    .formatted(expectedOverlap);

            // WHEN
            ThrowableAssert.ThrowingCallable throwingCallable = () -> Simulation.builder()
                    .withSize(3, 4)
                    .withMountains(mountains)
                    .withTreasures(treasures)
                    .withPlayers(players)
                    .build();

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

            String expectedErrorMessage = Simulation.FEATURES_COORDINATES_OUT_OF_BOUND_ERROR_MESSAGE
                    .formatted(expectedInvalidCoordinates);

            // WHEN
            ThrowableAssert.ThrowingCallable throwingCallable = () -> Simulation.builder()
                    .withSize(width, height)
                    .withMountains(mountains)
                    .withTreasures(treasures)
                    .withPlayers(players)
                    .build();

            // THEN
            assertThatThrownBy(throwingCallable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageStartingWith(expectedErrorMessage);
        }

        private static Stream<Arguments> throw_exception_if_feature_out_of_bound() {
            Coordinates coordinates = tooMuchSouth();
            Coordinates coordinates1 = tooMuchNorth();
            Coordinates coordinates2 = tooMuchEast();
            Coordinates coordinates3 = tooMuchWest();
            Arguments[] arguments = {
                    Arguments.of(width, height, of(new Mountain(coordinates3)), of(tooMuchWest())),
                    Arguments.of(width, height, of(new Mountain(coordinates2)), of(tooMuchEast())),
                    Arguments.of(width, height, of(new Mountain(coordinates1)), of(tooMuchNorth())),
                    Arguments.of(width, height, of(new Mountain(coordinates)), of(tooMuchSouth())),
                    multipleMountainsOutOfBounds(),
            };
            return Stream.of(arguments);
        }

        private static Arguments multipleMountainsOutOfBounds() {
            return Arguments.of(width,
                    height,
                    of(new Mountain(inBound()),
                            new Mountain(tooMuchSouth()),
                            new Mountain(tooMuchWest())),
                    of(tooMuchSouth(), tooMuchWest()));
        }

        private static Coordinates inBound() {
            return new Coordinates(1, 1);
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
}
