package org.example.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

/**
 * Could have been also used for writing results, although not necessary. Could have been replaced with a builder.
 */
@Getter
public class SimulationBuilder {
    private Simulation.Size size;
    private List<Mountain> mountains = new ArrayList<>();
    private List<Treasure> treasures = new ArrayList<>();
    private List<Player> players = new ArrayList<>();

    public Simulation build() {
        Simulation simulation = new Simulation(this.getSize(),
                this.getMountains(),
                this.getTreasures(),
                this.getPlayers());
        new SimulationValidator(simulation).validate();
        return simulation;
    }

    public SimulationBuilder withSize(int width, int height) {
        this.size = new Simulation.Size(width, height);
        return this;
    }

    public SimulationBuilder withSize(Simulation.Size size) {
        this.size = size;
        return this;
    }

    public SimulationBuilder withMountains(List<Mountain> mountains) {
        this.mountains = mountains;
        return this;
    }

    public SimulationBuilder withTreasures(List<Treasure> treasures) {
        this.treasures = treasures;
        return this;
    }

    public SimulationBuilder withPlayers(List<Player> players) {
        this.players = players;
        return this;
    }

    public SimulationBuilder addMountain(Mountain mountain) {
        this.mountains.add(mountain);
        return this;
    }

    public SimulationBuilder addTreasure(Treasure treasure) {
        this.treasures.add(treasure);
        return this;
    }

    public SimulationBuilder addPlayer(Player player) {
        this.players.add(player);
        return this;
    }


}
