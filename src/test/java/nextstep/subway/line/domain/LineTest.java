package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {
    @DisplayName("지하철 노선 생성")
    @Test
    void test_create() {
        // given & when
        Line newLine = new Line("신분당선", "bg-red-600");
        // then
        assertThat(newLine).isNotNull();
    }

    @DisplayName("지하철 노선 이름은 null 이거나 빈값일 수 없다.")
    @Test
    void test_not_null_name() {
        assertAll(
                () -> assertThatThrownBy(() -> new Line(null, "bg-red-600"))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> new Line("", "bg-red-600"))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("지하철 노선 색은 null 이거나 빈값일 수 없다.")
    @Test
    void test_not_null_color() {
        assertAll(
                () -> assertThatThrownBy(() -> new Line("신분당선", null))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> new Line("신분당선", ""))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }
}