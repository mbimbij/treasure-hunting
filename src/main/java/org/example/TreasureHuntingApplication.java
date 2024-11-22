package org.example;

import org.example.domain.Territory;
import org.example.infra.InputReader;
import org.example.infra.OutputWriter;
import org.example.infra.TerritoryData;
import org.example.infra.TerritoryFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TreasureHuntingApplication {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please provide 2 arguments input as a single argument");
            System.exit(1);
        }
        runSimulation(args[0], args[1]);
    }

    public static void runSimulation(String inputFilePath, String outputFilePath) {
        TerritoryData territoryData = InputReader.readFile(inputFilePath);
        Territory.Size size = territoryData.getSize();
        Territory territory = new Territory(size.width(),
                size.height(),
                territoryData.getMountains(),
                territoryData.getTreasures(),
                territoryData.getPlayers());
        territory.runSimulation();
        String formattedTerritory = new TerritoryFormatter().formatTerritory(territory);
        OutputWriter.writeToFile(formattedTerritory, outputFilePath);
    }
}
