package nextstep.subway.line.domain;

import nextstep.subway.line.domain.wrappers.Distance;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 지하철역 연결 테이블 테스트")
public class LineStationTest {

    private Station station;
    private Station preStation;
    private LineStation lineStation;

    @BeforeEach
    void setUp() {
        station = new Station(2L, "정자역");
        preStation = new Station(1L, "양재역");
        lineStation = new LineStation(station, preStation, 10);
    }

    @Test
    void 지하철_노선_지하철역_entity_생성() {
        assertThat(lineStation).isEqualTo(new LineStation(station, preStation, 10));
    }

    @Test
    void 지하철_노선_지하철역_entity_line_추가() {
        Line line = new Line("신분당선", "bg-red-600");
        lineStation.lineBy(line);
        assertThat(lineStation).isEqualTo(new LineStation(station, preStation, 10, line));
    }

    @Test
    @DisplayName("노선 지하철역 entity update")
    void update() {
        Station newStation = new Station(3L, "판교역");
        Distance newDistance = new Distance(6);

        lineStation.update(station, newStation, newDistance);
        assertThat(lineStation).isEqualTo(new LineStation(station, newStation, 6));
    }

    @Test
    @DisplayName("노선 지하철역 entity의 이전역 정보가 null인지 체크")
    void isNullPreStation() {
        assertThat(new LineStation(preStation, null, 0).isNullPreStation()).isTrue();
        assertThat(lineStation.isNullPreStation()).isFalse();
    }

    @Test
    @DisplayName("노선 지하철역 다음 노선 지하철역 여부 확인")
    void isNextLineStation() {
        LineStation nextLineStation = new LineStation(new Station(3L, "미금역"), station, 10);
        assertThat(nextLineStation.isNextLineStation(lineStation)).isTrue();
        assertThat(nextLineStation.isNextLineStation(new LineStation(preStation, station, 10))).isFalse();
    }

    @Test
    @DisplayName("노선 지하철역 이전역 정보 일치 여부")
    void isSamePreStation() {
        LineStation expected1 = new LineStation(station, preStation, 10);
        LineStation expected2 = new LineStation(preStation, station, 10);
        assertThat(lineStation.isSamePreStation(expected1)).isTrue();
        assertThat(lineStation.isSamePreStation(expected2)).isFalse();
    }

    @Test
    @DisplayName("두개의 노선 지하철역 entity의 이전 지하철역, 지하철역이 같은지 비교")
    void is_same() {
        LineStation other = new LineStation(station, preStation, 10);
        assertThat(lineStation.isSame(other)).isTrue();
        assertThat(lineStation.isSame(new LineStation(station, new Station(7L, "교대역"), 10))).isFalse();
        assertThat(lineStation.isSame(new LineStation(new Station(7L, "교대역"), preStation, 10))).isFalse();
    }

    @Test
    void 지하철_노선_entity가_첫번째_지하철_노선인지_확인() {
        LineStation lineStation1 = new LineStation(station, null, 10);
        LineStation lineStation2 = new LineStation(station, preStation, 10);
        assertThat(lineStation1.isFirstLineStation()).isTrue();
        assertThat(lineStation2.isFirstLineStation()).isFalse();
    }
}
