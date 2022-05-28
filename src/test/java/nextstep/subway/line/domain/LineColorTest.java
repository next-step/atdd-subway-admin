package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineColorTest {
    @DisplayName("지하철 노선 색은 null 이거나 빈값일 수 없다.")
    @Test
    void test_not_null() {
        assertAll(
                () -> assertThatThrownBy(() -> LineColor.from(null))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> LineColor.from(""))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }
}