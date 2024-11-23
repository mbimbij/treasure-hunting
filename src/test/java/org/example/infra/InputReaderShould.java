package org.example.infra;

import org.assertj.core.api.ThrowableAssert;
import org.example.TestDataFactory;
import org.example.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;
import static org.example.TestDataFactory.lara;
import static org.example.domain.Command.*;
import static org.example.domain.Orientation.NORTH;

class InputReaderShould {

    @Test
    void read_whole_file() {
        // GIVEN
        String filePath = "src/test/resources/input.txt";

        // WHEN
        SimulationBuilder simulationData = InputReader.readFile(filePath);

        // THEN
        assertThat(simulationData.getSize()).isEqualTo(TestDataFactory.defaultSimulationSize());
        assertThat(simulationData.getMountains()).isEqualTo(TestDataFactory.defaultMountains());
        assertThat(simulationData.getTreasures()).isEqualTo(TestDataFactory.defaultTreasures());
        assertThat(simulationData.getPlayers()).isEqualTo(of(lara()));
    }

    @Test
    void read_simulation_size() {
        // GIVEN
        SimulationBuilder simulationData = Simulation.builder();

        // WHEN
        InputReader.readLine("C - 3 - 4", simulationData);

        // THEN
        assertThat(simulationData).extracting(SimulationBuilder::getSize)
                .isNotNull()
                .isEqualTo(TestDataFactory.defaultSimulationSize());
    }

    @Test
    void throw_exception_if_read_simulation_size_but_already_defined() {
        // GIVEN
        SimulationBuilder simulationData = Simulation.builder();
        simulationData.withSize(2, 4);

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
        SimulationBuilder simulationData = Simulation.builder();

        // WHEN
        InputReader.readLine("M - 1 - 0", simulationData);
        InputReader.readLine("M - 2 - 1", simulationData);

        // THEN
        List<Mountain> expected = of(new Mountain(1, 0),
                new Mountain(2, 1));
        assertThat(simulationData.getMountains()).isEqualTo(expected);
    }

    @Test
    void read_treasures() {
        // GIVEN
        SimulationBuilder simulationData = Simulation.builder();

        // WHEN
        InputReader.readLine("T - 0 - 3 - 2", simulationData);
        InputReader.readLine("T - 1 - 3 - 3", simulationData);

        // THEN
        List<Treasure> expected = of(new Treasure(0, 3, 2),
                new Treasure(1, 3, 3));
        assertThat(simulationData.getTreasures()).isEqualTo(expected);
    }

    @Test
    void read_adventurers() {
        // GIVEN
        SimulationBuilder simulationData = Simulation.builder();

        // WHEN
        InputReader.readLine("A - Lara - 1 - 1 - S - AADADAGGA", simulationData);
        InputReader.readLine("A - Jones - 2 - 2 - N - ADGGAGDDGA", simulationData);

        // THEN
        List<Player> expected = of(lara(), jones());
        assertThat(simulationData.getPlayers())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expected);
    }

    private Player jones() {
        return new Player("Jones",
                new Coordinates(2, 2),
                NORTH,
                of(A, D, G, G, A, G, D, D, G, A));
    }

    @Test
    void ignore_lines_starting_with_hash() {
        // GIVEN
        SimulationBuilder simulationData = Simulation.builder();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> InputReader.readLine("# Comment", simulationData);

        // THEN
        assertThatCode(throwingCallable).doesNotThrowAnyException();
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
        SimulationBuilder builder = Simulation.builder();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> InputReader.readLine(line, builder);

        // THEN
        assertThatCode(throwingCallable)
                .doesNotThrowAnyException();
        assertThat(builder.getSize()).isNull();
        assertThat(builder.getMountains()).isEmpty();
        assertThat(builder.getTreasures()).isEmpty();
        assertThat(builder.getPlayers()).isEmpty();
    }
}
