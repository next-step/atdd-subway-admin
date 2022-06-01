package nextstep.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DistanceTest {
    @Test
    @DisplayName("Distance 생성")
    void distance(){
        Distance distance = Distance.from(10);
        assertThat(distance.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("Distance 생성 실패")
    void distance_exception(){
        assertThrows(IllegalArgumentException.class, () -> Distance.from(0));
    }
}