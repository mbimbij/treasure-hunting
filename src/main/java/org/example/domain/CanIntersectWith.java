package org.example.domain;

import java.util.Objects;

/**
 * TODO Refactor the signature so that it is <code>boolean collidesWith(CanCollideWith collidable);</code>, but postponed as it is a "nice-to-have"
 */
interface CanIntersectWith {
    default boolean intersectsWith(Coordinates otherCoordinates) {
        return Objects.equals(getCoordinates(), otherCoordinates);
    }

    Coordinates getCoordinates();
}
