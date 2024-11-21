package org.example;

public record Treasure(Coordinates coordinates, int quantity) {
    public Treasure(int weCoordinates, int nsCoordinates, int quantity) {
        this(new Coordinates(weCoordinates, nsCoordinates), quantity);
    }
}
