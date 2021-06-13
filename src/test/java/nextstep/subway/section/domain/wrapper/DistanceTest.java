package nextstep.subway.section.domain.wrapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 거리 원시값 포장 객체 테스트")
class DistanceTest {

    @Test
    void 구간_거리_원시값_포장_객체_생성() {
        Distance distance = new Distance(10);
        assertThat(distance).isEqualTo(new Distance(10));
    }

    @Test
    void 구간_거리_원시값_포장_객체에_음수_값이_들어올_경우_에러_발생() {
        assertThatThrownBy(() -> new Distance(-1)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간거리는 값은 음수가 입력할 수 없습니다.");
    }

    @Test
    void 두_구간_거리_빼기() {
        Distance distance1 = new Distance(10);
        Distance distance2 = new Distance(4);
        Distance actual = distance1.subtractionDistance(distance2);
        assertThat(actual.distance()).isEqualTo(6);
    }
}