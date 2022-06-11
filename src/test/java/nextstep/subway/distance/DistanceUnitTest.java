package nextstep.subway.distance;

import nextstep.subway.section.domain.Distance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class DistanceUnitTest {
    Distance distance_10;

    @BeforeEach
    void init() {
        distance_10 = new Distance(10);
    }

    @ParameterizedTest
    @CsvSource({"20,10,10", "30,20,10", "10,5,5"})
    @DisplayName("Distance minus 메소드 Happy Path")
    void minus(int inputFirst, int inputSecond, int result) {
        Distance distance = new Distance(inputFirst);
        distance.minus(inputSecond);
        assertThat(distance.getDistance()).isEqualTo(result);
    }

    @Test
    void 거리_10에서_거리_20을_뺀다() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            distance_10.minus(20);
        });
    }

    @Test
    void 거리_10에서_거리_20을_더한다() {
        distance_10.plus(20);
        assertThat(distance_10.getDistance()).isEqualTo(30);
    }
}
