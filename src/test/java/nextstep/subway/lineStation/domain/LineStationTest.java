package nextstep.subway.lineStation.domain;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 지하철역 연결 테이블 테스트")
public class LineStationTest {

    @Test
    void 지하철_노선_지하철역_entity_생성() {
        LineStation lineStation = new LineStation(2L, 1L, 10);
        assertThat(lineStation.getStationId()).isEqualTo(2L);
        assertThat(lineStation.getPreStationId()).isEqualTo(1L);
        assertThat(lineStation.getDistance()).isEqualTo(10);
    }

    @Test
    void 지하철_노선_지하철역_entity_line_추가() {
        LineStation lineStation = new LineStation(2L, 1L, 10);
        Line line = new Line("신분당선", "bg-red-600");
        lineStation.lineBy(line);
        assertThat(lineStation.getLine()).isEqualTo(line);
    }
}
