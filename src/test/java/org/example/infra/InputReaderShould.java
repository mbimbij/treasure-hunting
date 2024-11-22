package org.example.infra;

import org.assertj.core.api.ThrowableAssert;
import org.example.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;
import static org.example.domain.Command.*;
import static org.example.domain.Orientation.NORTH;
import static org.example.domain.Orientation.SOUTH;

class InputReaderShould {

    @Test
    void read_whole_file() {
        // GIVEN
        String filePath = "src/test/resources/test-input-1.txt";

        // WHEN
        TerritoryData territoryData = InputReader.readFile(filePath);

        // THEN
        assertThat(territoryData.getSize()).isEqualTo(new Territory.Size(3, 4));
        assertThat(territoryData.getMountains()).isEqualTo(getMountainsFromInstructions());
        assertThat(territoryData.getTreasures()).isEqualTo(getTreasuresFromInstructions());
        assertThat(territoryData.getPlayers()).isEqualTo(of(getPlayerLara()));
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
        assertThat(territoryData.getMountains()).isEqualTo(getMountainsFromInstructions());
    }

    @Test
    void read_treasures() {
        // GIVEN
        TerritoryData territoryData = new TerritoryData();

        // WHEN
        InputReader.readLine("T - 0 - 3 - 2", territoryData);
        InputReader.readLine("T - 1 - 3 - 3", territoryData);

        // THEN
        assertThat(territoryData.getTreasures()).isEqualTo(getTreasuresFromInstructions());
    }

    @Test
    void read_adventurers() {
        // GIVEN
        TerritoryData territoryData = new TerritoryData();

        // WHEN
        InputReader.readLine("A - Lara - 1 - 1 - S - AADADAGGA", territoryData);
        InputReader.readLine("A - Jones - 2 - 2 - N - ADGGAGDDGA", territoryData);

        // THEN
        Player lara = getPlayerLara();
        Player jones = new Player("Jones",
                new Coordinates(2, 2),
                NORTH,
                of(A, D, G, G, A, G, D, D, G, A));
        assertThat(territoryData.getPlayers())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(of(lara, jones));
    }

    @Test
    void ignore_lines_starting_with_hash() {
        // GIVEN
        TerritoryData territoryData = new TerritoryData();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> InputReader.readLine("# Comment", territoryData);

        // THEN
        assertThatCode(throwingCallable)
                .doesNotThrowAnyException();
        assertThat(territoryData.getSize()).isNull();
        assertThat(territoryData.getMountains()).isEmpty();
        assertThat(territoryData.getTreasures()).isEmpty();
        assertThat(territoryData.getPlayers()).isEmpty();
    }

    /**
     * I decided to apply a somewhat "tolerant reader" principle, and ignore any line that doesn't start with 'C', 'M',
     * 'T' or 'A'.
     * TODO Later on, verify that a warn log is written so that potential errors are not silently swept under the rug
     *
     * @param line
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "x - sk jdsjs",
            "noise",
            "",
    })
    void ignore_lines_starting_with_anything_else(String line) {
        // GIVEN
        TerritoryData territoryData = new TerritoryData();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> InputReader.readLine(line, territoryData);

        // THEN
        assertThatCode(throwingCallable)
                .doesNotThrowAnyException();
        assertThat(territoryData.getSize()).isNull();
        assertThat(territoryData.getMountains()).isEmpty();
        assertThat(territoryData.getTreasures()).isEmpty();
        assertThat(territoryData.getPlayers()).isEmpty();
    }

    private List<Mountain> getMountainsFromInstructions() {
        return of(new Mountain(1, 0),
                new Mountain(2, 1));
    }

    private List<Treasure> getTreasuresFromInstructions() {
        return of(new Treasure(0, 3, 2),
                new Treasure(1, 3, 3));
    }

    private Player getPlayerLara() {
        return new Player("Lara",
                new Coordinates(1, 1),
                SOUTH,
                of(A, A, D, A, D, A, G, G, A));
    }
}
