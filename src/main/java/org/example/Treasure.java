package org.example;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public final class Treasure implements CanCollideWith {
    public static final String CREATE_WITH_NEGATIVE_QUANTITY_ERROR_MESSAGE = "Cannot create treasures with negative quantity";
    public static final String COLLECT_EMPTY_TREASURE_ERROR_MESSAGE = "Cannot collect empty treasure";
    @EqualsAndHashCode.Include
    private final Coordinates coordinates;
    private int quantity;

    public Treasure(Coordinates coordinates, int quantity) {
        this.coordinates = coordinates;
        this.quantity = quantity;
        if(this.quantity < 0) {
            throw new IllegalArgumentException(CREATE_WITH_NEGATIVE_QUANTITY_ERROR_MESSAGE);
        }
    }

    public Treasure(int weCoordinates, int nsCoordinates, int quantity) {
        this(new Coordinates(weCoordinates, nsCoordinates), quantity);
    }

    public void collectTreasure() {
        if (this.isEmpty()) {
            throw new IllegalStateException(COLLECT_EMPTY_TREASURE_ERROR_MESSAGE);
        }
        this.quantity--;
    }

    @Override
    public Coordinates coordinates() {
        return coordinates;
    }

    public int quantity() {
        return quantity;
    }

    boolean isEmpty() {
        return quantity() <= 0;
    }
}
