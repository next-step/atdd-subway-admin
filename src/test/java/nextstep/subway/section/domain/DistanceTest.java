package nextstep.subway.section.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DistanceTest {

    @Test
    void shortenDistanceTest() {
        Distance distance = Distance.valueOf(10);
        distance.shorten(5);

        assertThat(distance.get()).isEqualTo(5);
    }

    @Test
    void 유효하지_않은_distanceTest() {
        assertThatThrownBy(() -> {
            Distance.valueOf(0);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            Distance.valueOf(10).shorten(10);
        }).isInstanceOf(IllegalArgumentException.class);
    }

}
