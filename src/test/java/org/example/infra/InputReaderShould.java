package org.example.infra;

import org.assertj.core.api.ThrowableAssert;
import org.example.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.domain.Command.*;
import static org.example.domain.Orientation.NORTH;
import static org.example.domain.Orientation.SOUTH;

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
        InputReader.readLine("C - 3 - 4", territoryData);

        // THEN
        assertThat(territoryData).extracting(TerritoryData::getSize)
                .isNotNull()
                .isEqualTo(new Territory.Size(3, 4));
    }

    @Test
    void throw_exception_if_read_territory_size_but_already_defined() {
        // GIVEN
        TerritoryData territoryData = new TerritoryData();
        territoryData.setSize(new Territory.Size(2, 4));

        // WHEN
        String line = "C - 3 - 4";
        ThrowableAssert.ThrowingCallable throwingCallable = () -> InputReader.readLine(line, territoryData);

        // THEN
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(InputReader.SIZE_ALREADY_DEFINED_ERROR_MESSAGE_FORMAT.formatted(line));
    }

    @Test
    void read_mountains() {
        // GIVEN
        TerritoryData territoryData = new TerritoryData();

        // WHEN
        InputReader.readLine("M - 1 - 0", territoryData);
        InputReader.readLine("M - 2 - 1", territoryData);

        // THEN
        List<Mountain> expectedMountains = of(new Mountain(1, 0),
                new Mountain(2, 1));
        assertThat(territoryData.getMountains()).isEqualTo(expectedMountains);
    }

    @Test
    void read_treasures() {
        // GIVEN
        TerritoryData territoryData = new TerritoryData();

        // WHEN
        InputReader.readLine("T - 0 - 3 - 2", territoryData);
        InputReader.readLine("T - 1 - 3 - 3", territoryData);

        // THEN
        List<Treasure> expectedTreasures = of(new Treasure(0, 3, 2),
                new Treasure(1, 3, 3));
        assertThat(territoryData.getTreasures()).isEqualTo(expectedTreasures);
    }

    @Test
    void read_adventurers() {
        // GIVEN
        TerritoryData territoryData = new TerritoryData();

        // WHEN
        InputReader.readLine("A - Lara - 1 - 1 - S - AADADAGGA", territoryData);
        InputReader.readLine("A - Jones - 2 - 2 - N - ADGGAGDDGA", territoryData);

        // THEN
        Player lara = new Player("Lara",
                new Coordinates(1, 1),
                SOUTH,
                of(A, A, D, A, D, A, G, G, A));
        Player jones = new Player("Jones",
                new Coordinates(2, 2),
                NORTH,
                of(A, D, G, G, A, G, D, D, G, A));
        List<Player> expectedPlayers = of(lara, jones);
        assertThat(territoryData.getPlayers())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expectedPlayers);
    }
}
