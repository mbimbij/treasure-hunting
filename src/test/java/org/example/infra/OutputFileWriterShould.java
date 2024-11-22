package org.example.infra;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.example.domain.Territory;
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
        OutputFileWriter outputFileWriter = new OutputFileWriter();

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

    private void setupAndVerifyOutputFile(Path outputDirPath, Path outputFilePath) throws IOException {
        Files.createDirectories(outputDirPath);
        Files.deleteIfExists(outputFilePath);
        Files.createFile(outputFilePath);
        assumeThat(Files.exists(outputFilePath)).isTrue();
        assertThat(Files.readString(outputFilePath, StandardCharsets.UTF_8)).isEmpty();
    }
}
