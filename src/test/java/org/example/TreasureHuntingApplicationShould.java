package org.example;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TreasureHuntingApplicationShould {
    @Test
    void can_create_world() {
        World world = new World();
    }
}
