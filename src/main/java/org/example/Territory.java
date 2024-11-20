package org.example;

import lombok.Getter;

/**
 * Represent 'Madre de Dios' territory. I opted for a noun for the class name and 'Madre de Dios' for the instance name.
 */
@Getter
public class Territory {
    private final int width;
    private final int height;

    public Territory(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
