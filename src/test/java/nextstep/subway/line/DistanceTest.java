package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.line.domain.Distance;

@DataJpaTest
public class DistanceTest {
    @DisplayName("지하철 구간 거리 빼기 기능")
    @Test
    void minus() {
        // given
        Distance distance = new Distance(10);
        Distance targetDistance = new Distance(5);
        distance = distance.minus(targetDistance);

        // when
        int result = distance.getDistance();

        // then
        assertThat(result).isEqualTo(5);
    }

    @DisplayName("지하철 구간길이 보다 더 큰 길이 뺴기")
    @Test
    void invalidMinus() {

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> {
                // given
                Distance distance = new Distance(10);
                Distance targetDistance = new Distance(15);

                // when
                distance.minus(targetDistance);
            }).withMessageMatching("구간의 거리가 같거나 더 멉니다.");
    }
}
