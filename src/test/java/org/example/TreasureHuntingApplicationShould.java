package org.example;

import lombok.SneakyThrows;
import org.example.domain.Territory;
import org.example.infra.InputReader;
import org.example.infra.OutputWriter;
import org.example.infra.TerritoryData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

class TreasureHuntingApplicationShould {

    /**
     * write an output file in "target" directory, as it is expected to be present, and allowed to be created or written
     * to, and is not version controlled.
     */
    @SneakyThrows
    @Test
    void run_entire_simulation() {
        // GIVEN
        String filePath = "src/test/resources/input.txt";
        Path outputFilePath = Paths.get("target", "output.txt");

        // WHEN
        TreasureHuntingApplication.runSimulation("src/test/resources/input.txt", "target/output.txt");

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
}
