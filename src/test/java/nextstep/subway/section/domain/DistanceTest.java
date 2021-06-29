package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DistanceTest {

    @DisplayName("거리가 1보다 작아 생성을 실패한다")
    @Test
    void create_TooSmall_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Distance(0)
        );
    }

    @DisplayName("두 거리의 차가 1보다 작아 수정할 수 없다.")
    @Test
    void subtract_TooSmall_ExceptionThrown() {
        Distance three = new Distance(3);
        Distance other = new Distance(3);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            three.subtract(other)
        );
    }

}
