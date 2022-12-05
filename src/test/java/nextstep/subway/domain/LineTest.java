package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
    @Test
    @DisplayName("Line 생성")
    void createLine() {
        Line actual = Line.of("신분당선", "bg-red-600");
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo("신분당선"),
            () -> assertThat(actual.getColor()).isEqualTo("bg-red-600")
        );
    }

    @Test
    @DisplayName("Line 생성시 이름, 색상 필수값")
    void createLineNotNullNameAndColor() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> Line.of(null, "bg-red-600")),
            () -> assertThrows(IllegalArgumentException.class, () -> Line.of("신분당선", null))
        );
    }

    @Test
    @DisplayName("노선 수정시 이름과 색상이 변경된다.")
    void updateLine() {
        Line actual = Line.of("신분당선", "bg-red-600");

        actual.updateLine("경춘선", "bg-emerald-600");

        assertAll(
            () -> assertThat(actual.getName()).isEqualTo("경춘선"),
            () -> assertThat(actual.getColor()).isEqualTo("bg-emerald-600")
        );
    }
}
