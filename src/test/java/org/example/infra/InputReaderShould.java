package org.example.infra;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputReaderShould {

    @Test
    void read_territory_size() {
        // WHEN
        TerritoryData territoryData = InputReader.readFile("src/test/resources/test-input-1.txt");

        // THEN
        assertThat(territoryData).extracting(TerritoryData::getWidth).isNotNull().isEqualTo(3);
        assertThat(territoryData).extracting(TerritoryData::getHeight).isNotNull().isEqualTo(4);
    }
}
