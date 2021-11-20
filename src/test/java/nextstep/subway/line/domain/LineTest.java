package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    @DisplayName("지하철 노선 생성시 NULL, BLANK 검사한다.")
    @Test
    void validateLine() {
        assertAll(
                () -> assertThatThrownBy(() -> {
                    new Line("2호선", null);
                }).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> {
                    new Line("", "green");
                }).isInstanceOf(IllegalArgumentException.class)
        );
    }
}
