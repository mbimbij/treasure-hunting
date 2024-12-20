package org.example;

import org.example.domain.*;

import java.util.List;

import static java.util.List.of;
import static org.example.domain.Command.*;
import static org.example.domain.Orientation.SOUTH;

public class TestDataFactory {
    public static Simulation.Size defaultSimulationSize() {
        return new Simulation.Size(3, 4);
    }

    public static List<Mountain> defaultMountains() {
        return of(new Mountain(1, 0),
                new Mountain(2, 1));
    }

    public static List<Treasure> defaultTreasures() {
        return of(new Treasure(0, 3, 2),
                new Treasure(1, 3, 3));
    }

    public static Player lara() {
        return new Player("Lara",
                new Coordinates(1, 1),
                SOUTH,
                of(A, A, D, A, D, A, G, G, A));
    }
}
