package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class LineTest {

    @DisplayName("이름, 색깔 정보로 Line 을 생성한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "1호선,BLUE", "2호선,GREEN"})
    void create1(String name, String color) {
        // when
        Line line = Line.of(name, color);

        // then
        assertAll(
            () -> assertEquals(line.getName(), LineName.from(name)),
            () -> assertEquals(line.getColor(), LineColor.from(color))
        );
    }

    @DisplayName("Line 이름을 null or 빈 문자열로 할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create2(String name) {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Line.of(name, "RED"))
                                            .withMessageContaining("노선이름이 비어있습니다");
    }

    @DisplayName("Line 색깔을 null or 빈 문자열로 할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create3(String color) {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Line.of("신분당선", color))
                                            .withMessageContaining("노선색깔이 비어있습니다.");
    }
}