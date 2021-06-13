package nextstep.subway.wrappers;

import nextstep.subway.line.domain.Line;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("노선 지하철 연결 테이블 일급 컬렉션 객체 테스트")
public class LineStationsTest {

    private Station station;
    private Station preStation;

    @BeforeEach
    void setUp() {
        station = new Station(2L, "정자역");
        preStation = new Station(1L, "양재역");
    }

    @Test
    void 노선_지하철_연결_테이블_일급_컬렉션_객체_생성() {
        LineStations lineStations = new LineStations(Arrays.asList(new LineStation(station, preStation, 10)));
        assertThat(lineStations).isEqualTo(new LineStations(Arrays.asList(new LineStation(station, preStation, 10))));
    }

    @Test
    void 노선_지하철_연결_테이블_일급_컬렉션_객체_포함_여부() {
        LineStations lineStations = new LineStations(Arrays.asList(new LineStation(station, preStation, 10)));
        LineStation lineStation = new LineStation(new Station(3L, "판교역"), station, 10);
        assertThat(lineStations.contains(new LineStation(station, preStation, 10))).isTrue();
        assertThat(lineStations.contains(lineStation)).isFalse();
    }

    @Test
    void 노선_지하철_연결_테이블_일급_컬렉션_객체_추가() {
        LineStation lineStation1 = new LineStation(station, preStation, 10);
        LineStation lineStation2 = new LineStation(new Station(3L, "판교역"), station, 100);
        List<LineStation> lineStations = new ArrayList<>();
        lineStations.add(lineStation1);
        LineStations actual = new LineStations(lineStations);

        actual.addLineStation(lineStation2);
        assertThat(actual).isEqualTo(new LineStations(Arrays.asList(lineStation1, lineStation2)));
    }

    @Test
    void 노선_지하철_연결_테이블_일급_컬렉션_중복_객체_추가() {
        LineStation lineStation1 = new LineStation(station, preStation, 10);
        List<LineStation> lineStations = new ArrayList<>();
        lineStations.add(lineStation1);
        LineStations actual = new LineStations(lineStations);

        actual.addLineStation(lineStation1);
        assertThat(actual).isEqualTo(new LineStations(Arrays.asList(lineStation1)));
    }

    @Test
    void 노선_지하철_연결_entity_상행부터_정렬한_리스트_반환() {
        LineStation lineStation1 = new LineStation(station, preStation, 10);
        LineStation lineStation2 = new LineStation(preStation, null, 0);
        LineStations lineStations = new LineStations(Arrays.asList(lineStation1, lineStation2));
        List<LineStation> actual = lineStations.getLineStationsOrderByAsc();
        assertThat(actual.get(0)).isEqualTo(lineStation2);
        assertThat(actual.get(1)).isEqualTo(lineStation1);
    }

    @Test
    void 노선_지하철_연결_테이블_일급_컬렉션_노선_정보_추가() {
        LineStation lineStation1 = new LineStation(station, preStation, 10);
        LineStation lineStation2 = new LineStation(preStation, null, 0);
        LineStations lineStations = new LineStations(Arrays.asList(lineStation1, lineStation2));
        lineStations.addLine(new Line("신분당선", "bg - red - 600"));
        for (LineStation lineStation : lineStations.getLineStationsOrderByAsc()) {
            assertThat(lineStation.getLine()).isNotNull();
        }
    }

    @Test
    void 노선_지하철_연결_테이블_첫번째_노선_지하철_객체_update() {
        LineStation lineStation1 = new LineStation(station, preStation, 10);
        LineStation lineStation2 = new LineStation(preStation, null, 0);
        LineStations lineStations = new LineStations(Arrays.asList(lineStation1, lineStation2));
        LineStation expected = new LineStation(preStation, new Station(7L, "교대역"), 10);
        lineStations.updateFirstLineStation(expected);
        assertThat(lineStations.contains(expected)).isTrue();
    }

    @Test
    void 노선_지하철_연결_테이블_이전역_기준_노선_지하철_객체_찾기() {
        LineStation lineStation1 = new LineStation(station, preStation, 10);
        LineStation lineStation2 = new LineStation(preStation, null, 0);
        LineStations lineStations = new LineStations(Arrays.asList(lineStation1, lineStation2));

        LineStation actual = lineStations.findLineStationByPreStation(preStation);
        assertThat(actual).isEqualTo(lineStation1);
    }

    @Test
    void 노선_지하철_연결_테이블_이전역_기준_노선_지하철_객체_찾을수_없을때_에러_발생() {
        LineStation lineStation1 = new LineStation(station, preStation, 10);
        LineStation lineStation2 = new LineStation(preStation, null, 0);
        LineStations lineStations = new LineStations(Arrays.asList(lineStation1, lineStation2));

        assertThatThrownBy(() -> lineStations.findLineStationByPreStation(station)).isInstanceOf(IllegalArgumentException.class);
    }
}