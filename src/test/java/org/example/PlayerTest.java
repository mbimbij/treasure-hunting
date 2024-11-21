package org.example;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerTest {
    @Test
    void equalityTest() {
        Player adventurer1 = new Player("adventurer1",
                new Coordinates(0, 1),
                Orientation.NORTH,
                0);
        Player adventurer1Bis = new Player("adventurer1",
                new Coordinates(1, 2),
                Orientation.NORTH,
                0);
        Player adventurer2 = new Player("adventurer2",
                new Coordinates(1, 2),
                Orientation.NORTH,
                0);

        assertThat(adventurer1).isEqualTo(adventurer1Bis);
        assertThat(adventurer1).isNotEqualTo(adventurer2);
        assertThat(adventurer1Bis).isNotEqualTo(adventurer2);
    }
}
