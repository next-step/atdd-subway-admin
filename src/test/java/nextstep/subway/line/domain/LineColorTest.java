package nextstep.subway.line.domain;

import nextstep.subway.common.message.ExceptionMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LineColorTest {

    @DisplayName("지하철 노선 색상 값이 null 이거나 empty 이면 LineColor 객체 생성 시 예외가 발생한다.")
    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @NullAndEmptySource
    void nullOrEmptyColor(String input) {
        Assertions.assertThatThrownBy(() -> LineColor.from(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith(ExceptionMessage.REQUIRED);
    }

    @DisplayName("지하철 노선 색상 값이 null 과 empty 가 아니면 LineColor 객체가 정상적으로 생성된다.")
    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @ValueSource(strings = {"red", "yellow"})
    void createLineColor(String input) {
        LineColor color = LineColor.from(input);

        Assertions.assertThat(color).isNotNull();
    }

    @DisplayName("지하철 노선 색상 값이 다른 LineColor 객체는 동등하지 않다.")
    @Test
    void equalsFail() {
        LineColor color1 = LineColor.from("red");
        LineColor color2 = LineColor.from("yellow");

        Assertions.assertThat(color1).isNotEqualTo(color2);
    }

    @DisplayName("지하철 노선 색상 값이 같은 LineColor 객체는 동등하다.")
    @Test
    void equalsSuccess() {
        LineColor color1 = LineColor.from("red");
        LineColor color2 = LineColor.from("red");

        Assertions.assertThat(color1).isEqualTo(color2);
    }
}
