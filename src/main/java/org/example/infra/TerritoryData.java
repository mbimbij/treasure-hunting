package org.example.infra;

import lombok.Data;
import org.example.domain.Mountain;
import org.example.domain.Player;
import org.example.domain.Territory;
import org.example.domain.Treasure;

import java.util.ArrayList;
import java.util.List;

@Data
public class TerritoryData {
    private Territory.Size size;
    private List<Mountain> mountains = new ArrayList<>();
    private List<Treasure> treasures = new ArrayList<>();
    private List<Player> players;

    public void addMountain(Mountain mountain) {
        this.mountains.add(mountain);
    }

    public void addTreasure(Treasure treasure) {
        this.treasures.add(treasure);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void parse(String line) {
        String[] split = line.split("-");
        Territory.Size size = new Territory.Size(Integer.parseInt(split[1].trim()),
                Integer.parseInt(split[2].trim()));
        this.setSize(size);
    }
}
