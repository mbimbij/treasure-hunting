package org.example;

public record Mountain(Coordinates coordinates) {
    public Mountain(int weCoordinates, int nsCoordinates) {
        this(new Coordinates(weCoordinates, nsCoordinates));
    }

}
