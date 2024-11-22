package org.example;

import org.example.domain.Simulation;
import org.example.infra.InputReader;
import org.example.infra.OutputWriter;
import org.example.infra.SimulationData;
import org.example.infra.SimulationFormatter;

public class TreasureHuntingApplication {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Please provide 2 arguments: input and output files");
            System.exit(1);
        }
        runSimulation(args[0], args[1]);
    }

    public static void runSimulation(String inputFilePath, String outputFilePath) {
        SimulationData simulationData = InputReader.readFile(inputFilePath);
        Simulation simulation = new Simulation(simulationData.getSize(),
                simulationData.getMountains(),
                simulationData.getTreasures(),
                simulationData.getPlayers());
        simulation.run();
        String formatted = new SimulationFormatter().formatSimulation(simulation);
        OutputWriter.writeToFile(formatted, outputFilePath);
    }
}
