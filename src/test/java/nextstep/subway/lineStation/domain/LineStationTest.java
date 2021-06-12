package nextstep.subway.lineStation.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 지하철역 연결 테이블 테스트")
public class LineStationTest {

    private Station station;
    private Station preStation;

    @BeforeEach
    void setUp() {
        station = new Station(2L, "정자역");
        preStation = new Station(1L, "양재역");
    }

    @Test
    void 지하철_노선_지하철역_entity_생성() {
        LineStation lineStation = new LineStation(station, preStation, 10);
        assertThat(lineStation).isEqualTo(new LineStation(station, preStation, 10));
    }

    @Test
    void 지하철_노선_지하철역_entity_line_추가() {
        LineStation lineStation = new LineStation(station, preStation, 10);
        Line line = new Line("신분당선", "bg-red-600");
        lineStation.lineBy(line);
        assertThat(lineStation).isEqualTo(new LineStation(station, preStation, 10, line));
    }
}
