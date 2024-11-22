package org.example.domain;

public record Mountain(Coordinates coordinates) implements CanCollideWith {
    public Mountain(int weCoordinates, int nsCoordinates) {
        this(new Coordinates(weCoordinates, nsCoordinates));
    }
}
