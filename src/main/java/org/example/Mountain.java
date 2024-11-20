package org.example;

public record Mountain(Coordinates coordinates) {
    public static Mountain plop(int westEastCoordinate, int northSouthCoordinate){
        return new Mountain(new Coordinates(westEastCoordinate, northSouthCoordinate));
    }
}
