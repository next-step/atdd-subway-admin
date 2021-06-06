package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 노선 파라미터 테스트")
class LineRequestTest {

    @Test
    @DisplayName("지하철 노선 파라미터를 이용하여 Line entity 정상 생성")
    void toLine() {
        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600");
        assertThat(lineRequest.toLine()).isEqualTo(new Line("분당선", "bg-red-600"));
    }
}