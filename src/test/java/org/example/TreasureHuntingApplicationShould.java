package org.example;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.IntegerArbitrary;
import org.assertj.core.api.ThrowableAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TreasureHuntingApplicationShould {
    @Property
    void create_territory_with_specified_size(@ForAll("validPairsOfWidthAndHeight") IntegerPair pair) {
        // GIVEN
        Integer width = pair.first();
        Integer height = pair.second();

        // WHEN
        Territory madreDeDios = new Territory(width, height);

        // THEN
        assertThat(madreDeDios.getWidth()).isEqualTo(width);
        assertThat(madreDeDios.getHeight()).isEqualTo(height);
    }

    @Property
    void throw_exception_for_invalid_width_or_height(@ForAll("invalidPairsOfWidthAndHeight") IntegerPair pair) {
        // GIVEN
        Integer width = pair.first();
        Integer height = pair.second();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Territory(width, height);

        // THEN
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Width and height must be greater than zero");
    }

    @Provide
    Arbitrary<IntegerPair> validPairsOfWidthAndHeight() {
        IntegerArbitrary integers = Arbitraries.integers().between(-100, 100);
        return Combinators.combine(integers, integers)
                .filter((integer, integer2) -> integer > 0 && integer2 > 0)
                .as(IntegerPair::new);
    }

    @Provide
    Arbitrary<IntegerPair> invalidPairsOfWidthAndHeight() {
        IntegerArbitrary integers = Arbitraries.integers().between(-100, 100);
        return Combinators.combine(integers, integers)
                .filter((integer, integer2) -> integer <= 0 || integer2 <= 0)
                .as(IntegerPair::new);
    }

    record IntegerPair(Integer first, Integer second) {
    }
}
