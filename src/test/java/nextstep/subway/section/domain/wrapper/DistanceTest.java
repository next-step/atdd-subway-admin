package nextstep.subway.section.domain.wrapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 거리 원시값 포장 객체 테스트")
class DistanceTest {

    @Test
    void 구간_거리_원시값_포장_객체_생성() {
        Distance distance = new Distance(10);
        assertThat(distance).isEqualTo(new Distance(10));
    }
}