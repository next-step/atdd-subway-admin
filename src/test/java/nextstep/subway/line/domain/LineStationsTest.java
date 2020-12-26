package nextstep.subway.line.domain;

import nextstep.subway.line.exception.LineStationDuplicatedException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @DisplayName("상행 순으로 지하철 역을 정렬하여 가져옵니다.")
    @Test
    void getStationsOrderByUp() {
        // given
        LineStations lineStations = new LineStations();

        Station 청량리역 = new Station("청량리역");
        Station 신도림역 = new Station("신도림역");
        Station 관악역 = new Station("관악역");
        Station 금정역 = new Station("금정역");

        Section section1 = Section.of(청량리역, 신도림역, 10);
        Section section2 = Section.of(신도림역, 관악역, 10);
        Section section3 = Section.of(관악역, 금정역, 10);

        lineStations.add(new LineStation(line, section1));
        lineStations.add(new LineStation(line, section2));
        lineStations.add(new LineStation(line, section3));

        // when
        List<Station> stationsOrderByUp = lineStations.getStationsOrderByUp();

        // then
        assertThat(stationsOrderByUp).containsExactly(청량리역, 신도림역, 관악역, 금정역);
    }
}
