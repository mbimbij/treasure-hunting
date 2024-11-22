package org.example.infra;

import org.example.domain.Orientation;

public enum OrientationInput {
    N,S,E,W;

    public Orientation toDomainValue() {
        return switch (this){
            case N -> Orientation.NORTH;
            case S -> Orientation.SOUTH;
            case E -> Orientation.EAST;
            case W -> Orientation.WEST;
        };
    }
}
