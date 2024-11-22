package org.example.infra;

import org.example.domain.Orientation;

public enum OrientationData {
    N,S,E,W;

    public Orientation toDomainValue() {
        return switch (this){
            case N -> Orientation.NORTH;
            case S -> Orientation.SOUTH;
            case E -> Orientation.EAST;
            case W -> Orientation.WEST;
        };
    }

    public static OrientationData from(Orientation orientation) {
        return switch (orientation){
            case NORTH -> N;
            case EAST -> E;
            case SOUTH -> S;
            case WEST -> W;
        };
    }
}
