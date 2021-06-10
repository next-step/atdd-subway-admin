package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.dto.LineResponse;

class LineTest {

    @Test
    @DisplayName(value = "Line entity 에서 LineResponse Dto를 만들어낸다")
    void createResponseDto() {
        Line line = new Line("1호선", "blue");
        assertThat(line.toResponse()).isEqualTo(LineResponse.of(line));
    }

}