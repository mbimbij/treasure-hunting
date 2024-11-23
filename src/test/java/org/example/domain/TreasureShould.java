package org.example.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TreasureShould {

    @Test
    void not_allow_negative_quantity() {
        assertThatThrownBy(() -> new Treasure(0, 0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(Treasure.CREATE_WITH_NEGATIVE_QUANTITY_ERROR_MESSAGE);
    }

    @Test
    void not_allow_collecting_treasure_if_empty() {
        Treasure treasure = new Treasure(0, 0, 0);
        assertThatThrownBy(treasure::collectTreasure)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(Treasure.COLLECT_EMPTY_TREASURE_ERROR_MESSAGE);
    }
}
