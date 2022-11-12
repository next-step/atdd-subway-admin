package nextstep.subway.line.domain;

import nextstep.subway.line.exception.LineExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 클래스 테스트")
class LineTest {

    @Test
    void 동등성_테스트() {
        assertEquals(new Line("신분당선", "bg-red-600"), new Line("신분당선", "bg-red-600"));
    }

    @Test
    void 지하철_노선_객체_생성시_name이_null이면_IllegalArgumentException_발생() {
        assertThatThrownBy(() -> {
            new Line(null, "bg-red-600");
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LineExceptionCode.REQUIRED_NAME.getMessage());
    }

    @Test
    void 지하철_노선_객체_생성시_color가_null이면_IllegalArgumentException_발생() {
        assertThatThrownBy(() -> {
            new Line("신분당선", null);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LineExceptionCode.REQUIRED_COLOR.getMessage());
    }

    @Test
    void 지하철_노선_수정시_name이_null이면_IllegalArgumentException_발생() {
        assertThatThrownBy(() -> {
           Line line = new Line("신분당선", "bg-red-600");
           line.update(null, "bg-red-600");
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LineExceptionCode.REQUIRED_NAME.getMessage());
    }

    @Test
    void 지하철_노선_수정시_color가_null이면_IllegalArgumentException_발생() {
        assertThatThrownBy(() -> {
            Line line = new Line("신분당선", "bg-red-600");
            line.update("신분당선", null);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LineExceptionCode.REQUIRED_COLOR.getMessage());
    }
}
