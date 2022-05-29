package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineNameTest {
    @DisplayName("지하철 노선 이름은 null 이거나 빈값일 수 없다.")
    @Test
    void test_not_null() {
        assertAll(
                () -> assertThatThrownBy(() -> LineName.from(null))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> LineName.from(""))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }
}