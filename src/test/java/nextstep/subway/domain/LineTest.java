package nextstep.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("지하철 노선 생성 시 필수값 테스트 (이름)")
    @Test
    void createLine1() {
        Assertions.assertThatThrownBy(() -> Line.of(null, "bg-red-600"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선 생성 시 필수값 테스트 (색상)")
    @Test
    void createLine2() {
        Assertions.assertThatThrownBy(() -> Line.of("신분당선", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선 생성 (정상케이스)")
    @Test
    void createLine3() {
        Line line = Line.of("신분당선", "bg-red-500");
        Assertions.assertThat(line).isNotNull();
    }

    @DisplayName("지하철 노선 동등성 테스트")
    @Test
    void equals1() {
        Line actual = Line.of("신분당선", "bg-red-500");
        Line expected = Line.of("신분당선", "bg-red-500");

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지하철 노선 동등성 테스트2")
    @Test
    void equals2() {
        Line actual = Line.of("신분당선", "bg-red-500");
        Line expected = Line.of("분당선", "bg-red-500");

        Assertions.assertThat(actual).isNotEqualTo(expected);
    }
}