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
        Section section = createSection(new Station("청량리역"), new Station("신창역"), Distance.valueOf(100));
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
        Section section = createSection(new Station("청량리역"), new Station("신창역"), Distance.valueOf(100));
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
        Section section = createSection(new Station("청량리역"), new Station("신창역"), Distance.valueOf(100));
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

        Section section1 = createSection(청량리역, 신도림역, Distance.valueOf(100));
        Section section2 = createSection(신도림역, 관악역, Distance.valueOf(100));
        Section section3 = createSection(관악역, 금정역, Distance.valueOf(100));

        lineStations.add(new LineStation(line, section1));
        lineStations.add(new LineStation(line, section2));
        lineStations.add(new LineStation(line, section3));

        // when
        List<Station> stationsOrderByUp = lineStations.getStationsOrderByUp();

        // then
        assertThat(stationsOrderByUp).containsExactly(청량리역, 신도림역, 관악역, 금정역);
    }

    @DisplayName("새로운 지하철 구간을 지하철 노선 구간으로 등록한다.")
    @Test
    void addSection() {
        // given
        LineStations lineStations = new LineStations();
        Station 청량리역 = new Station("청량리역");
        Station 신창역 = new Station("신창역");
        Section section = createSection(청량리역, 신창역, Distance.valueOf(100));
        LineStation lineStation = new LineStation(line, section);
        lineStations.add(lineStation);

        // when
        Station 회기역 = new Station("회기역");
        Section newSection1 = createSection(회기역, 청량리역, Distance.valueOf(10));
        lineStations.add(line, newSection1);

        Station 신도림역 = new Station("신도림역");
        Section newSection2 = createSection(청량리역, 신도림역, Distance.valueOf(10));
        lineStations.add(line, newSection2);

        Station 당정역 = new Station("당정역");
        Section newSection3 = createSection(당정역, 신창역, Distance.valueOf(10));
        lineStations.add(line, newSection3);

        Station 창신역 = new Station("창신역");
        Section newSection4 = createSection(신창역, 창신역, Distance.valueOf(10));
        lineStations.add(line, newSection4);


        // then
        assertThat(lineStations.getStationsOrderByUp()).containsExactly(회기역, 청량리역, 신도림역, 당정역, 신창역, 창신역);
    }

    private Section createSection(final Station upStation, final Station downStation, final Distance distance) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }
}
