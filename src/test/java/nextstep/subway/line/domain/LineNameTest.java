package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("노선 이름")
class LineNameTest {
    @Test
    @DisplayName("노선 이름을 생성할 수 있다.")
    void 노선_이름_생성_성공() {
        assertThat(new LineName("신분당선")).isNotNull();
    }

    @Nested
    @DisplayName("노선 이름 생성 실패")
    class 노선_이름_생성_실패 {
        @Test
        @DisplayName("노선 이름은 Null이 될 수 없다.")
        void NULL_노선_이름() {
            assertThatThrownBy(() -> new LineName(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("노선 이름은 공백이 될 수 없다.")
        void 공백_노선_이름() {
            assertThatThrownBy(() -> new LineName("")).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
