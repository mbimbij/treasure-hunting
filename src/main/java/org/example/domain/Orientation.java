package org.example.domain;

public enum Orientation {
    NORTH, EAST, SOUTH, WEST;

    public Orientation leftOf() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }

    public Orientation rightOf() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };

    }
}
