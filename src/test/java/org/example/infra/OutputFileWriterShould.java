package org.example.infra;

import lombok.SneakyThrows;
import org.example.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.example.TestDataFactory.*;

class OutputFileWriterShould {

    private OutputFileWriter outputFileWriter;

    @BeforeEach
    void setUp() {
        outputFileWriter = new OutputFileWriter();
    }

    /**
     * write an output file in "target" directory, as it is expected to be present, and allowed to be created or written
     * to, and is not version controlled.
     */
    @SneakyThrows
    @Test
    void write_simulation_results_to_file() {
        // GIVEN
        Territory.Size size = sizeFromInstructions();
        Territory territory = new Territory(size.width(),
                size.height(),
                mountainsFromInstructions(),
                treasuresFromInstructions(),
                of(playerLara()));

        Path outputDirPath = Paths.get("target");
        Path outputFilePath = Paths.get("target", "output.txt");
        setupAndVerifyOutputFile(outputDirPath, outputFilePath);

        // WHEN
        outputFileWriter.writeToFile(territory, outputFilePath);

        // THEN
        String outputFileContent = Files.readString(outputFilePath, StandardCharsets.UTF_8);
        assertThat(outputFileContent).isEqualTo("""
                C - 3 - 4
                M - 1 - 0
                M - 2 - 1
                T - 1 - 3 - 2
                A - Lara - 0 - 3 - S - 3
                """);
    }

    @SneakyThrows
    @Test
    void format_entire_simulation_results() {
        // GIVEN a setup similar to the instructions, BUT without player and treasure overlap AND an empty treasure chest
        Territory.Size size = sizeFromInstructions();
        Territory territory = new Territory(size.width(),
                size.height(),
                mountainsFromInstructions(),
                of(new Treasure(0, 3, 0),
                        new Treasure(1, 3, 3)
                ),
                of(playerLara()));

        // WHEN
        String formattedTerritory = outputFileWriter.formatTerritory(territory);

        // THEN
        assertThat(formattedTerritory).isEqualTo("""
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
        Territory.Size size = sizeFromInstructions();

        // WHEN
        String formattedSize = outputFileWriter.formatSize(size);

        // THEN
        assertThat(formattedSize).isEqualTo("C - 3 - 4");
    }

    @Test
    void format_mountain_appropriately() {
        // GIVEN
        Mountain mountain = new Mountain(1, 0);

        // WHEN
        String formattedMountain = outputFileWriter.formatMountain(mountain);

        // THEN
        assertThat(formattedMountain).isEqualTo("M - 1 - 0");
    }

    @Test
    void format_treasure_appropriately() {
        // GIVEN
        Treasure treasure = new Treasure(1, 0, 2);

        // WHEN
        String formattedTreasure = outputFileWriter.formatTreasure(treasure);

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
        String formattedTreasures = outputFileWriter.formatTreasures(treasures);

        // THEN
        assertThat(formattedTreasures).isEqualTo("""
                T - 1 - 0 - 2
                T - 1 - 2 - 3""");
    }

    @Test
    void format_player_appropriately() {
        // GIVEN
        Player player = playerLaraAfterSimulation();

        // WHEN
        String formattedPlayer = outputFileWriter.formatPlayer(player);

        // THEN
        assertThat(formattedPlayer).isEqualTo("A - Lara - 0 - 3 - S - 3");
    }

    private void setupAndVerifyOutputFile(Path outputDirPath, Path outputFilePath) throws IOException {
        Files.createDirectories(outputDirPath);
        Files.deleteIfExists(outputFilePath);
        Files.createFile(outputFilePath);
        assumeThat(Files.exists(outputFilePath)).isTrue();
        assertThat(Files.readString(outputFilePath, StandardCharsets.UTF_8)).isEmpty();
    }
}
