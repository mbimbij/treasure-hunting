package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.Orientation.*;

class PlayerShould {
    @Test
    void verify_expected_equality_properties() {
        Player adventurer1 = new Player("adventurer1",
                new Coordinates(0, 1),
                NORTH);
        Player adventurer1Bis = new Player("adventurer1",
                new Coordinates(1, 2),
                NORTH);
        Player adventurer2 = new Player("adventurer2",
                new Coordinates(1, 2),
                NORTH);

        assertThat(adventurer1).isEqualTo(adventurer1Bis);
        assertThat(adventurer1).isNotEqualTo(adventurer2);
        assertThat(adventurer1Bis).isNotEqualTo(adventurer2);
    }

    @ParameterizedTest
    @MethodSource
    void move_forward(Player player, Coordinates expectedCoordinates, Orientation expectedOrientation) {
        player.moveForward();
        assertThat(player.getCoordinates()).isEqualTo(expectedCoordinates);
        assertThat(player.getOrientation()).isEqualTo(expectedOrientation);
    }

    static Stream<Arguments> move_forward(){
        Player facingNorth = new Player("player", new Coordinates(1, 1), NORTH);
        Player facingEast = new Player("player", new Coordinates(1, 1), EAST);
        Player facingSouth = new Player("player", new Coordinates(1, 1), SOUTH);
        Player facingWest = new Player("player", new Coordinates(1, 1), WEST);
        Arguments[] arguments = {
                Arguments.of(facingNorth, new Coordinates(1, 0), NORTH),
                Arguments.of(facingEast, new Coordinates(2, 1), EAST),
                Arguments.of(facingSouth, new Coordinates(1, 2), SOUTH),
                Arguments.of(facingWest, new Coordinates(0, 1), WEST),
        };
        return Stream.of(arguments);
    }
}