package org.example;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdventurerTest {
    @Test
    void equalityTest() {
        Adventurer adventurer1 = new Adventurer("adventurer1",
                new Coordinates(0, 1),
                Orientation.NORTH,
                0);
        Adventurer adventurer1Bis = new Adventurer("adventurer1",
                new Coordinates(1, 2),
                Orientation.NORTH,
                0);
        Adventurer adventurer2 = new Adventurer("adventurer2",
                new Coordinates(1, 2),
                Orientation.NORTH,
                0);

        assertThat(adventurer1).isEqualTo(adventurer1Bis);
        assertThat(adventurer1).isNotEqualTo(adventurer2);
        assertThat(adventurer1Bis).isNotEqualTo(adventurer2);
    }
}
