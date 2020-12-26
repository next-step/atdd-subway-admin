package nextstep.subway.line.domain;

import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 공장에 대한 테스트")
class LineFactoryTest {

    @DisplayName("지하철 노선 공장은 지하철 노선을 생성합니다.")
    @Test
    void create() {
        // given
        LineFactory lineFactory = lineRequest -> Line.of("1호선", "blue");

        LineRequest lineRequest = new LineRequest();

        // when
        Line line = lineFactory.create(lineRequest);

        // then
        assertThat(line).isNotNull();
    }

}
