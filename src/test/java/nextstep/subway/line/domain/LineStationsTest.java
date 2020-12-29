package nextstep.subway.line.domain;

import nextstep.subway.line.exception.BadSectionException;
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

    private LineStations lineStations;

    @BeforeEach
    void setUp() {
        line = LineTest.지하철_1호선_생성됨();
        lineStations = line.getLineStations();
    }


    @DisplayName("지하철 노선 구간을 추가한다.")
    @Test
    void add() {
        // given
        Section section = createSection(new Station("청량리역"), new Station("신도림역"), Distance.valueOf(10));

        // when
        lineStations.add(new LineStation(line, section));

        // then
        assertThat(lineStations.getLineStations()).contains(new LineStation(line, section));
    }

    @DisplayName("이미 등록된 지하철 노선 구간은 추가할 수 없다.")
    @Test
    void addFail() {
        // given
        Section section = createSection(new Station("청량리역"), new Station("신창역"), Distance.valueOf(10));

        // when / then
        assertThrows(BadSectionException.class, () -> lineStations.add(new LineStation(line, section)));
    }

    @DisplayName("상행 순으로 지하철 역을 정렬하여 가져옵니다.")
    @Test
    void getStationsOrderByUp() {
        // given
        Station 청량리역 = new Station("청량리역");
        Station 신도림역 = new Station("신도림역");
        Station 관악역 = new Station("관악역");
        Station 금정역 = new Station("금정역");
        Station 신창역 = new Station("신창역");

        Section section1 = createSection(청량리역, 신도림역, Distance.valueOf(10));
        Section section2 = createSection(신도림역, 관악역, Distance.valueOf(10));
        Section section3 = createSection(관악역, 금정역, Distance.valueOf(10));

        lineStations.add(new LineStation(line, section1));
        lineStations.add(new LineStation(line, section2));
        lineStations.add(new LineStation(line, section3));

        // when
        List<Station> stationsOrderByUp = lineStations.getOrderedStations();

        // then
        assertThat(stationsOrderByUp).containsExactly(청량리역, 신도림역, 관악역, 금정역, 신창역);
    }

    @DisplayName("새로운 지하철 구간을 지하철 노선 구간으로 등록한다.")
    @Test
    void addSection() {
        // given
        Station 청량리역 = new Station("청량리역");
        Station 신창역 = new Station("신창역");

        // when
        Station 회기역 = new Station("회기역");
        Section newSection1 = createSection(회기역, 청량리역, Distance.valueOf(10));
        lineStations.add(new LineStation(line, newSection1));

        Station 신도림역 = new Station("신도림역");
        Section newSection2 = createSection(청량리역, 신도림역, Distance.valueOf(10));
        lineStations.add(new LineStation(line, newSection2));

        Station 당정역 = new Station("당정역");
        Section newSection3 = createSection(당정역, 신창역, Distance.valueOf(10));
        lineStations.add(new LineStation(line, newSection3));

        Station 창신역 = new Station("창신역");
        Section newSection4 = createSection(신창역, 창신역, Distance.valueOf(10));
        lineStations.add(new LineStation(line, newSection4));


        // then
        assertThat(lineStations.getOrderedStations()).containsExactly(회기역, 청량리역, 신도림역, 당정역, 신창역, 창신역);
    }

    private Section createSection(final Station upStation, final Station downStation, final Distance distance) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }
}
