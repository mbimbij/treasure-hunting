package org.example;

import org.example.domain.Territory;
import org.example.infra.InputReader;
import org.example.infra.OutputWriter;
import org.example.infra.TerritoryData;
import org.example.infra.TerritoryFormatter;

public class TreasureHuntingApplication {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Please provide 2 arguments: input and output files");
            System.exit(1);
        }
        runSimulation(args[0], args[1]);
    }

    public static void runSimulation(String inputFilePath, String outputFilePath) {
        TerritoryData territoryData = InputReader.readFile(inputFilePath);
        Territory territory = new Territory(territoryData.getSize(),
                territoryData.getMountains(),
                territoryData.getTreasures(),
                territoryData.getPlayers());
        territory.runSimulation();
        String formattedTerritory = new TerritoryFormatter().formatTerritory(territory);
        OutputWriter.writeToFile(formattedTerritory, outputFilePath);
    }
}
