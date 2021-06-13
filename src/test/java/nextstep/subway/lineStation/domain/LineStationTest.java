package nextstep.subway.lineStation.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.wrappers.Distance;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(lineStation.isSamePreStation(preStation)).isTrue();
        assertThat(lineStation.isSamePreStation(station)).isFalse();
    }

    @ParameterizedTest
    @DisplayName("노선 지하철역 entity들의 구간 거리가 크거나 같을 경우 에러 발생")
    @ValueSource(ints = {10, 11})
    void valid_distance(int distance) {
        LineStation lineStation = new LineStation(new Station(5L, "시민의 숲역"), preStation, distance);
        assertThatThrownBy(() -> this.lineStation.validDistance(lineStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간 사이에 새로운 역을 등록 시 구간거리는 기존 등록된 구간 거리보다 작아야합니다.");
    }
}
