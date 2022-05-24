package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import nextstep.subway.constants.LineExceptionMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class LineTest {

    @DisplayName("지하철 노선을 이름과 노선 색상으로 생성할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "분당선,YELLOW"})
    void generate01(String name, String color) {
        // given & when
        Line line = Line.of(name, color);

        // then
        assertAll(
            () -> assertEquals(line.getName(), LineName.from(name)),
            () -> assertEquals(line.getColor(), LineColor.from(color))
        );
    }

    @DisplayName("지하철 노선 생성 시 이름이 공란일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void generate02(String name) {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Line.of(name, "RED"))
            .withMessageContaining(LineExceptionMessage.LINE_NAME_IS_NOT_NULL);
    }

    @DisplayName("지하철 노선 생성 시 노선 색상이 공란일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void generate03(String color) {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Line.of("신분당선", color))
            .withMessageContaining(LineExceptionMessage.LINE_COLOR_IS_NOT_NULL);
    }
}