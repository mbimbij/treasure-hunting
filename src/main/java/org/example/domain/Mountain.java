package org.example.domain;

public record Mountain(Coordinates getCoordinates) implements CanIntersectWith {
    public Mountain(int weCoordinates, int nsCoordinates) {
        this(new Coordinates(weCoordinates, nsCoordinates));
    }
}
