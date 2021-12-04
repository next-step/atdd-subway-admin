package nextstep.subway.line.domain;

import nextstep.subway.common.exception.NegativeNumberDistanceException;
import nextstep.subway.line.domain.Distance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 거리 도메인 관련")
class DistanceTest {

    @DisplayName("구간 거리를 저장할 수 있다.")
    @Test
    void createDistance() {
        // when
        final Distance distance = new Distance(9);

        // then
        assertThat(distance.getDistance()).isEqualTo(9);
    }

    @DisplayName("음수 구간 거리는 저장할 수 없다.")
    @Test
    void negativeDistanceException() {
        // then
        assertThatThrownBy(() -> {
            final Distance distance = new Distance(-1);

        }).isInstanceOf(NegativeNumberDistanceException.class)
        .hasMessageContaining("현재 계산된 거리 값이 음수입니다. : -1");
    }


}