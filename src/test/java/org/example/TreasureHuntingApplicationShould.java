package org.example;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class TreasureHuntingApplicationShould {
    @Test
    void can_create_world() {
        // GIVEN
        int width = 3;
        int height =4;

        // WHEN
        Territory madreDeDios = new Territory(width, height);

        // THEN
        SoftAssertions.assertSoftly(sa -> {
            sa.assertThat(madreDeDios.getWidth()).isEqualTo(width);
            sa.assertThat(madreDeDios.getHeight()).isEqualTo(height);
        });
    }
}
