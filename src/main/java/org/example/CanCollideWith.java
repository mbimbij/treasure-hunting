package org.example;

import java.util.Objects;

/**
 * TODO Refactor the signature so that it is <code>boolean collidesWith(CanCollideWith collidable);</code>, but postponed as it is a "nice-to-have"
 */
public interface CanCollideWith {
    default boolean collidesWith(Coordinates otherCoordinates){
        return Objects.equals(coordinates(), otherCoordinates);
    }
    Coordinates coordinates();
}
