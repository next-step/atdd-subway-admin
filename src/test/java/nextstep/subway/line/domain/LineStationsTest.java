package nextstep.subway.line.domain;

import nextstep.subway.line.exception.LineStationDuplicatedException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("지하철 노선 구간들에 대한 테스트")
class LineStationsTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = LineTest.지하철_1호선_생성됨();
    }


    @DisplayName("지하철 노선 구간을 추가한다.")
    @Test
    void add() {
        // given
        LineStations lineStations = new LineStations();
        Section section = Section.of(new Station("청량리역"), new Station("신창역"), 100);
        LineStation lineStation = new LineStation(line, section);

        // when
        lineStations.add(lineStation);

        // then
        assertThat(lineStations.getLineStations()).containsExactly(lineStation);
    }

    @DisplayName("이미 등록된 지하철 노선 구간은 추가할 수 없다.")
    @Test
    void addFail() {
        // given
        LineStations lineStations = new LineStations();
        Section section = Section.of(new Station("청량리역"), new Station("신창역"), 100);
        LineStation lineStation = new LineStation(line, section);
        lineStations.add(lineStation);

        // when / then
        assertThrows(LineStationDuplicatedException.class, () -> lineStations.add(lineStation));
    }

    @DisplayName("지하철 노선 구간이 등록되어 있는지 확인할 수 있다.")
    @Test
    void contain() {
        // given
        LineStations lineStations = new LineStations();
        Section section = Section.of(new Station("청량리역"), new Station("신창역"), 100);
        LineStation lineStation = new LineStation(line, section);
        lineStations.add(lineStation);


        // when
        boolean contains = lineStations.contains(lineStation);

        // then
        assertThat(contains).isTrue();
    }
}
