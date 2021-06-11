package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

class LineTest {

    @Test
    @DisplayName(value = "Line entity 에서 LineResponse Dto를 만들어낸다")
    void createResponseDto() {
        Line line = new Line("1호선", "blue");
        assertThat(line.toResponse()).isEqualTo(LineResponse.of(line));
    }

    @Test
    @DisplayName(value = "LineRequest 를 통해 Line 의 attribute 를 갱신한다")
    void updateLineAttribute() {
        Line line = new Line("5호선", "purple");
        LineRequest updateRequest = new LineRequest("6호선", "bg-brown-100");
        line.update(updateRequest);
        assertThat(line.getColor()).isEqualTo("bg-brown-100");
        assertThat(line.getName()).isEqualTo("6호선");
    }

}