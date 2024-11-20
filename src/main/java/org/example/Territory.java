package org.example;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent 'Madre de Dios' territory. I opted for a noun for the class name and 'Madre de Dios' for the instance
 * name.
 */
@Getter
public class Territory {
    private final int width;
    private final int height;
    private final List<Mountain> mountains;

    /**
     * Decided on an all-args constructor instead of a Builder for example. We have all the data at once after reading
     * the file, and while builders are "elegant", in my experience they tend to hide issues when adding or removing
     * data, which happens a lot in the early phases of a project and going back and forth on the modelling of the
     * domain. Granted the call to a validation method in the build() method could possibly address this, a constructor
     * is simpler and "fails fast(-er)".
     * <p>
     * But in the end one or the other is good enough imo, i'm open to discussion, compromise and changing my tune
     * according to feedback and whether this design gets buy-in from the team or not.
     *
     * @param width
     * @param height
     * @param mountains
     */
    public Territory(int width, int height, List<Mountain> mountains) {
        this.width = width;
        this.height = height;
        this.mountains = mountains;
        validate();
    }

    private void validate() {
        if (this.width <= 0 || this.height <= 0) {
            throw new IllegalArgumentException("Width and height must be greater than zero");
        }
    }
}
