package org.example.infra;

import lombok.SneakyThrows;
import org.example.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.TestDataFactory.*;
import static org.example.domain.Orientation.SOUTH;

class SimulationFormatterShould {

    private final SimulationFormatter formatter = new SimulationFormatter();

    @SneakyThrows
    @Test
    void format_entire_simulation_results() {
        // GIVEN a setup similar to the instructions, BUT without player and treasure overlap AND an empty treasure chest
        List<Treasure> treasures = of(new Treasure(0, 3, 0),
                new Treasure(1, 3, 3));
        Simulation simulation = Simulation.builder()
                .withSize(defaultSimulationSize())
                .withMountains(defaultMountains())
                .withTreasures(treasures)
                .withPlayers(of(lara()))
                .build();

        // WHEN
        String formattedSimulation = formatter.formatSimulation(simulation);

        // THEN
        assertThat(formattedSimulation).isEqualTo("""
                C - 3 - 4
                M - 1 - 0
                M - 2 - 1
                T - 1 - 3 - 3
                A - Lara - 1 - 1 - S - 0
                """);
    }

    @Test
    void format_simulation_size_appropriately() {
        // GIVEN
        Simulation.Size size = defaultSimulationSize();

        // WHEN
        String formattedSize = formatter.formatSize(size);

        // THEN
        assertThat(formattedSize).isEqualTo("C - 3 - 4");
    }

    @Test
    void format_mountain_appropriately() {
        // GIVEN
        Mountain mountain = new Mountain(1, 0);

        // WHEN
        String formattedMountain = formatter.formatMountain(mountain);

        // THEN
        assertThat(formattedMountain).isEqualTo("M - 1 - 0");
    }

    @Test
    void format_treasure_appropriately() {
        // GIVEN
        Treasure treasure = new Treasure(1, 0, 2);

        // WHEN
        String formattedTreasure = formatter.formatTreasure(treasure);

        // THEN
        assertThat(formattedTreasure).isEqualTo("T - 1 - 0 - 2");
    }

    /**
     * Tons of ways to verify it:
     * <p>
     * - verify a method printing a single treasure is not called for the empty one
     * <p>
     * - verify the whole file afterward
     * <p>
     * - verify printing an empty treasure returns an empty string and verifying the overall formatting in another test
     * <p>
     * - etc.
     * <p>
     * i opted for verifying the "treasures" section of the output file, assuming i will build a string composed of the
     * size, mountains, treasures, players sections, and write the whole content at once instead of writing line by
     * line. It is fine enough, as the file is small enough, but line by line and not keeping the string in memory would
     * be more appropriate if the simulation size were to grow, or performance issues were to appear.
     */
    @Test
    void skip_empty_treasure_when_printing_treasures() {
        // GIVEN
        List<Treasure> treasures = of(
                new Treasure(1, 0, 2),
                new Treasure(1, 2, 3),
                new Treasure(1, 1, 0)
        );

        // WHEN
        String formattedTreasures = formatter.formatTreasures(treasures);

        // THEN
        assertThat(formattedTreasures).isEqualTo("""
                T - 1 - 0 - 2
                T - 1 - 2 - 3""");
    }

    @Test
    void format_player_appropriately() {
        // GIVEN
        Player defaultPlayerAfterSimulation = lara()
                .withCoordinates(new Coordinates(0, 3))
                .withOrientation(SOUTH)
                .withCollectedTreasuresCount(3);

        // WHEN
        String formattedPlayer = formatter.formatPlayer(defaultPlayerAfterSimulation);

        // THEN
        assertThat(formattedPlayer).isEqualTo("A - Lara - 0 - 3 - S - 3");
    }
}
