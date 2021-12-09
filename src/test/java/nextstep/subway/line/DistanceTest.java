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

    @DisplayName("역 간 거리는 0보다 커야한다.")
    @Test
    void greaterThanZero() {
        assertThatThrownBy(() -> {
            new Distance(0);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 간 거리는 0보다 커야합니다.");
    }

    @DisplayName("거리끼리는 뺄셈을 할 수 있다.")
    @Test
    void minus() {
        //when
        Distance distance1 = distance.minus(3);
        Distance distance2 = distance1.minus(2);

        //then
        assertThat(distance2).isEqualTo(new Distance(5));
    }

    @DisplayName("거리끼리는 덧셈을 할 수 있다.")
    @Test
    void plus() {
        //when
        Distance distance1 = distance.plus(3);
        Distance distance2 = distance1.plus(2);

        //then
        assertThat(distance2).isEqualTo(new Distance(15));
    }

    @DisplayName("신규 역 간 거리가 더 크면 예외 발생")
    @Test
    void validateLargerThan() {
        assertThatThrownBy(() -> {
            distance.validateLargerThan(12);
        }).isInstanceOf(CannotUpdateSectionException.class)
                .hasMessage("신규 역 간 거리가 기존 역 간 거리와 같거나 더 클 수 없습니다.");
    }
}
