package nextstep.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DistanceTest {
    @DisplayName("정수 값을 가진다.")
    @Test
    void createTest() {
        assertThat(new Distance(3L)).isEqualTo(new Distance(3L));
    }

    @DisplayName("음수 값을 가지면 에러를 발생한다.")
    @Test
    void invalidTest() {
        assertThatThrownBy(() -> new Distance(-1L))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
