package org.example.infra;

import org.example.domain.Territory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputReaderShould {

    @Test
    void read_whole_file() {
        // WHEN
        TerritoryData territoryData = InputReader.readFile("src/test/resources/test-input-1.txt");

        // THEN
        Territory.Size expected = new Territory.Size(3, 4);
        assertThat(territoryData)
                .extracting(TerritoryData::getSize)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    void read_territory_size() {
        // GIVEN
        TerritoryData territoryData = new TerritoryData();

        // WHEN
        territoryData.parse("C - 3 - 4");

        // THEN
        assertThat(territoryData).extracting(TerritoryData::getSize)
                .isNotNull()
                .isEqualTo(new Territory.Size(3, 4));
    }
}
