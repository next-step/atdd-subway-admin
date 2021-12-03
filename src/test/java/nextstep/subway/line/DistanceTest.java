package nextstep.subway.line;

import nextstep.subway.Exception.CannotUpdateSectionException;
import nextstep.subway.line.domain.Distance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DistanceTest {
    private Distance distance;

    @BeforeEach
    void setUp() {
        distance = new Distance(10);
    }

    @DisplayName("역과 역 사이에 새로운 역이 추가되면 구간 거리 뺄셈")
    @Test
    void minus() {
        //when
        distance.minus(3);
        distance.minus(2);

        //then
        assertThat(distance.getDistance()).isEqualTo(5);
    }

    @DisplayName("역과 역 사이에 새로운 역이 추가될 때, 기존 역 간 거리가 더 크면 true 반환")
    @Test
    void isGreaterThan() {
        //when
        boolean result = distance.isGreaterThan(7);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("역과 역 사이에 새로운 역이 추가될 때, 기존 역 간 거리가 작거나 같을 경우 예외 발생")
    @Test
    void isGreaterThan_Exception() {
        //when
        assertThatThrownBy(() -> {
            distance.isGreaterThan(11);
        }).isInstanceOf(CannotUpdateSectionException.class).hasMessage("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
    }
}
