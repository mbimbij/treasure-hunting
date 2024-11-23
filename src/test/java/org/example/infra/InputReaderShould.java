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
        SimulationBuilder builder = InputReader.readFile(filePath);

        // THEN
        assertThat(builder.getSize()).isEqualTo(TestDataFactory.defaultSimulationSize());
        assertThat(builder.getMountains()).isEqualTo(TestDataFactory.defaultMountains());
        assertThat(builder.getTreasures()).isEqualTo(TestDataFactory.defaultTreasures());
        assertThat(builder.getPlayers()).isEqualTo(of(lara()));
    }

    @Test
    void read_simulation_size() {
        // GIVEN
        SimulationBuilder builder = Simulation.builder();

        // WHEN
        InputReader.readLine("C - 3 - 4", builder);

        // THEN
        assertThat(builder).extracting(SimulationBuilder::getSize)
                .isNotNull()
                .isEqualTo(TestDataFactory.defaultSimulationSize());
    }

    @Test
    void throw_exception_if_read_simulation_size_but_already_defined() {
        // GIVEN
        SimulationBuilder builder = Simulation.builder().withSize(2, 4);

        // WHEN
        String line = "C - 3 - 4";
        ThrowableAssert.ThrowingCallable throwingCallable = () -> InputReader.readLine(line, builder);

        // THEN
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(InputReader.SIZE_ALREADY_DEFINED_ERROR_MESSAGE_FORMAT.formatted(line));
    }

    @Test
    void read_mountains() {
        // GIVEN
        SimulationBuilder builder = Simulation.builder();

        // WHEN
        InputReader.readLine("M - 1 - 0", builder);
        InputReader.readLine("M - 2 - 1", builder);

        // THEN
        List<Mountain> expected = of(new Mountain(1, 0),
                new Mountain(2, 1));
        assertThat(builder.getMountains()).isEqualTo(expected);
    }

    @Test
    void read_treasures() {
        // GIVEN
        SimulationBuilder builder = Simulation.builder();

        // WHEN
        InputReader.readLine("T - 0 - 3 - 2", builder);
        InputReader.readLine("T - 1 - 3 - 3", builder);

        // THEN
        List<Treasure> expected = of(new Treasure(0, 3, 2),
                new Treasure(1, 3, 3));
        assertThat(builder.getTreasures()).isEqualTo(expected);
    }

    @Test
    void read_adventurers() {
        // GIVEN
        SimulationBuilder builder = Simulation.builder();

        // WHEN
        InputReader.readLine("A - Lara - 1 - 1 - S - AADADAGGA", builder);
        InputReader.readLine("A - Jones - 2 - 2 - N - ADGGAGDDGA", builder);

        // THEN
        List<Player> expected = of(lara(), jones());
        assertThat(builder.getPlayers())
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
        SimulationBuilder builder = Simulation.builder();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> InputReader.readLine("# Comment", builder);

        // THEN
        assertThatCode(throwingCallable).doesNotThrowAnyException();
        assertThat(builder.getSize()).isNull();
        assertThat(builder.getMountains()).isEmpty();
        assertThat(builder.getTreasures()).isEmpty();
        assertThat(builder.getPlayers()).isEmpty();
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
