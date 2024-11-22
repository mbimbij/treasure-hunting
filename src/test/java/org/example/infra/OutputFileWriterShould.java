package org.example.infra;

import lombok.SneakyThrows;
import org.example.domain.Mountain;
import org.example.domain.Territory;
import org.example.domain.Treasure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
                List.of(playerLara()));

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
        String formattedSize = outputFileWriter.formatMountain(mountain);

        // THEN
        assertThat(formattedSize).isEqualTo("M - 1 - 0");
    }

    @Test
    void format_treasure_appropriately() {
        // GIVEN
        Treasure treasure = new Treasure(1, 0, 2);

        // WHEN
        String formattedSize = outputFileWriter.formatTreasure(treasure);

        // THEN
        assertThat(formattedSize).isEqualTo("T - 1 - 0 - 2");
    }

    private void setupAndVerifyOutputFile(Path outputDirPath, Path outputFilePath) throws IOException {
        Files.createDirectories(outputDirPath);
        Files.deleteIfExists(outputFilePath);
        Files.createFile(outputFilePath);
        assumeThat(Files.exists(outputFilePath)).isTrue();
        assertThat(Files.readString(outputFilePath, StandardCharsets.UTF_8)).isEmpty();
    }
}
