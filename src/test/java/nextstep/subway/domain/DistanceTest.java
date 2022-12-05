package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DistanceTest {

    @Test
    @DisplayName("거리 생성")
    void createDistance() {
        Distance actual = Distance.from(10);
        assertThat(actual.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("거리는 0보다 커야한다")
    void createDistanceGraterThanZero() {
        assertAll(
            () -> assertThat(Distance.from(1)).isInstanceOf(Distance.class),
            () -> assertThrows(IllegalArgumentException.class, () -> Distance.from(0)),
            () -> assertThrows(IllegalArgumentException.class, () -> Distance.from(-1))
        );
    }
}