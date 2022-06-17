package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("노선 색상")
class LineColorTest {
    @Test
    @DisplayName("노선 색상을 생성할 수 있다.")
    void 노선_색상_생성_성공() {
        assertThat(new LineColor("bg-red-600")).isNotNull();
    }

    @Nested
    @DisplayName("노선 색상 생성 실패")
    class 노선_색상_생성_실패 {
        @Test
        @DisplayName("노선 색상은 Null이 될 수 없다.")
        void NULL_노선_색상() {
            assertThatThrownBy(() -> new LineColor(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("노선 색상은 공백이 될 수 없다.")
        void 공백_노선_색상() {
            assertThatThrownBy(() -> new LineColor("")).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
