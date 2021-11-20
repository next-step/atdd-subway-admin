package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineTest {

    private static final String LINE_NAME1 = "2호선";
    private static final String LINE_NAME2 = "5호선";
    private static final String LINE_COLOR1 = "orange darken-4";
    private static final String LINE_COLOR2 = "yellow darken-3";
    private static final Line LINE = new Line(LINE_NAME1, LINE_COLOR1);


    @Test
    @DisplayName("Line 생성 후 name,color 검증")
    void create() {
        // given
        // when
        Line actual = new Line(LINE_NAME1, LINE_COLOR1);

        // then
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(LINE_NAME1),
            () -> assertThat(actual.getColor()).isEqualTo(LINE_COLOR1)
        );
    }

    @Test
    @DisplayName("update 메소드 호출 후 변경된 name,color 일치 검증")
    void update() {
        // given
        Line actual = new Line(LINE_NAME2, LINE_COLOR2);

        // when
        actual.update(LINE);

        // then
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(LINE.getName()),
            () -> assertThat(actual.getColor()).isEqualTo(LINE.getColor())
        );
    }

    @Test
    @DisplayName("Line 생성 시 name,color 는 빈값일 경우 에러 발생")
    void validEmpty() {
        assertAll(
            () -> assertThrows(InvalidParameterException.class, () -> new Line("", LINE_COLOR1)),
            () -> assertThrows(InvalidParameterException.class, () -> new Line(LINE_NAME1, ""))
        );
    }
}
