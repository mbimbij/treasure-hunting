package org.example;

public record Coordinates(int westEastCoordinate, int northSouthCoordinate) {
    @Override
    public String toString() {
        return "(%d, %d)".formatted(westEastCoordinate, northSouthCoordinate);
    }
}
