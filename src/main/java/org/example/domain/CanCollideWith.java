package org.example.domain;

import java.util.Objects;

/**
 * TODO Refactor the signature so that it is <code>boolean collidesWith(CanCollideWith collidable);</code>, but postponed as it is a "nice-to-have"
 */
interface CanCollideWith {
    default boolean collidesWith(Coordinates otherCoordinates){
        return Objects.equals(coordinates(), otherCoordinates);
    }
    Coordinates coordinates();
}
