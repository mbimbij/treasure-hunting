package org.example.domain;

import lombok.With;

@With
public record Coordinates(int westEast, int northSouth) {
    @Override
    public String toString() {
        return "(%d, %d)".formatted(westEast, northSouth);
    }

    public Coordinates northOf() {
        return this.withNorthSouth(northSouth - 1);
    }

    public Coordinates southOf() {
        return this.withNorthSouth(northSouth + 1);
    }

    public Coordinates eastOf() {
        return this.withWestEast(westEast + 1);
    }

    public Coordinates westOf() {
        return this.withWestEast(westEast - 1);
    }

}
