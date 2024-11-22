package org.example.infra;

import org.assertj.core.api.ThrowableAssert;
import org.example.TestDataFactory;
import org.example.domain.Coordinates;
import org.example.domain.Player;
import org.example.domain.Simulation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;
import static org.example.domain.Command.*;
import static org.example.domain.Orientation.NORTH;

class InputReaderShould {

    @Test
    void read_whole_file() {
        // GIVEN
        String filePath = "src/test/resources/input.txt";

        // WHEN
        SimulationData simulationData = InputReader.readFile(filePath);

        // THEN
        assertThat(simulationData.getSize()).isEqualTo(TestDataFactory.sizeFromInstructions());
        assertThat(simulationData.getMountains()).isEqualTo(TestDataFactory.mountainsFromInstructions());
        assertThat(simulationData.getTreasures()).isEqualTo(TestDataFactory.treasuresFromInstructions());
        assertThat(simulationData.getPlayers()).isEqualTo(of(TestDataFactory.playerLara()));
    }

    @Test
    void read_simulation_size() {
        // GIVEN
        SimulationData simulationData = new SimulationData();

        // WHEN
        InputReader.readLine("C - 3 - 4", simulationData);

        // THEN
        assertThat(simulationData).extracting(SimulationData::getSize)
                .isNotNull()
                .isEqualTo(TestDataFactory.sizeFromInstructions());
    }

    @Test
    void throw_exception_if_read_simulation_size_but_already_defined() {
        // GIVEN
        SimulationData simulationData = new SimulationData();
        simulationData.setSize(new Simulation.Size(2, 4));

        // WHEN
        String line = "C - 3 - 4";
        ThrowableAssert.ThrowingCallable throwingCallable = () -> InputReader.readLine(line, simulationData);

        // THEN
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(InputReader.SIZE_ALREADY_DEFINED_ERROR_MESSAGE_FORMAT.formatted(line));
    }

    @Test
    void read_mountains() {
        // GIVEN
        SimulationData simulationData = new SimulationData();

        // WHEN
        InputReader.readLine("M - 1 - 0", simulationData);
        InputReader.readLine("M - 2 - 1", simulationData);

        // THEN
        assertThat(simulationData.getMountains()).isEqualTo(TestDataFactory.mountainsFromInstructions());
    }

    @Test
    void read_treasures() {
        // GIVEN
        SimulationData simulationData = new SimulationData();

        // WHEN
        InputReader.readLine("T - 0 - 3 - 2", simulationData);
        InputReader.readLine("T - 1 - 3 - 3", simulationData);

        // THEN
        assertThat(simulationData.getTreasures()).isEqualTo(TestDataFactory.treasuresFromInstructions());
    }

    @Test
    void read_adventurers() {
        // GIVEN
        SimulationData simulationData = new SimulationData();

        // WHEN
        InputReader.readLine("A - Lara - 1 - 1 - S - AADADAGGA", simulationData);
        InputReader.readLine("A - Jones - 2 - 2 - N - ADGGAGDDGA", simulationData);

        // THEN
        Player lara = TestDataFactory.playerLara();
        Player jones = new Player("Jones",
                new Coordinates(2, 2),
                NORTH,
                of(A, D, G, G, A, G, D, D, G, A));
        assertThat(simulationData.getPlayers())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(of(lara, jones));
    }

    @Test
    void ignore_lines_starting_with_hash() {
        // GIVEN
        SimulationData simulationData = new SimulationData();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> InputReader.readLine("# Comment", simulationData);

        // THEN
        assertThatCode(throwingCallable)
                .doesNotThrowAnyException();
        assertThat(simulationData.getSize()).isNull();
        assertThat(simulationData.getMountains()).isEmpty();
        assertThat(simulationData.getTreasures()).isEmpty();
        assertThat(simulationData.getPlayers()).isEmpty();
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
        SimulationData simulationData = new SimulationData();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> InputReader.readLine(line, simulationData);

        // THEN
        assertThatCode(throwingCallable)
                .doesNotThrowAnyException();
        assertThat(simulationData.getSize()).isNull();
        assertThat(simulationData.getMountains()).isEmpty();
        assertThat(simulationData.getTreasures()).isEmpty();
        assertThat(simulationData.getPlayers()).isEmpty();
    }
}
