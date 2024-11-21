package org.example;

public record Coordinates(int westEast, int northSouth) {
    @Override
    public String toString() {
        return "(%d, %d)".formatted(westEast, northSouth);
    }

    boolean isOutOfBound(int width, int height) {
        return westEast() < 0
               || westEast() >= width
               || northSouth() < 0
               || northSouth() >= height;
    }
}
