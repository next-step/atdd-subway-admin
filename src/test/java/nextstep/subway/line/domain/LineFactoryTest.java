package nextstep.subway.line.domain;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 공장에 대한 테스트")
class LineFactoryTest {

    @DisplayName("지하철 노선 공장은 지하철 노선을 생성합니다.")
    @Test
    void create() {
        // given
        LineFactory lineFactory = lineRequest -> {
            LastStation lastStation = new LastStation(new Station("청량리역"), new Station("신창역"));
            return Line.of("1호선", "blue", 100, lastStation);
        };

        LineRequest lineRequest = new LineRequest();

        // when
        Line line = lineFactory.create(lineRequest);

        // then
        assertThat(line).isNotNull();
    }

}
