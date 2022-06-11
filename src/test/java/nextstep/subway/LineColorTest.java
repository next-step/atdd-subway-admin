package nextstep.subway;

import nextstep.subway.domain.LineColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineColorTest {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("지하철노선 색상 값 비어있을 경우 Exception 발생 확인")
    void validate_empty(String color) {
        assertThatThrownBy(() -> {
            new LineColor(color);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"blue", "red", "green"})
    @DisplayName("지하철노선 색상 생성후 값 비교 확인")
    void validateStringNumber(String expected) {
        LineColor lineColor = new LineColor(expected);
        assertThat(lineColor.getColor()).isEqualTo(expected);
    }
}
