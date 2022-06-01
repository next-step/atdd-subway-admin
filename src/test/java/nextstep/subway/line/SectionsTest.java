package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선에 속한 구간 관련 테스트")
class SectionsTest {
    Station stationA;
    Station stationB;
    Line line;

    @DisplayName("상행종점역과 하행종점역을 갖는 노선을 추가한다.")
    @BeforeEach
    void setUp() {
        stationA = new Station("강남역");
        stationB = new Station("교대역");
        line = new Line("2호선", "green");
        line.addSection(stationA, stationB, 10);
    }

    @DisplayName("구간 중간에 구간을 추가한다.")
    @Test
    void addSectionLeft() {
        Station station = new Station("서초역");
        line.addSection(stationA, station, 3);
        assertThat(line.stations()).containsSequence(stationA, station, stationB);
    }

    @DisplayName("구간 중간에 구간을 추가한다.")
    @Test
    void addSectionRight() {
        Station station = new Station("서초역");
        line.addSection(station, stationB, 3);
        assertThat(line.stations()).containsSequence(stationA, station, stationB);
    }

    @DisplayName("상행 종점구간을 추가한다.")
    @Test
    void addSectionBegin() {
        Station station = new Station("서초역");
        line.addSection(station, stationA, 3);
        assertThat(line.stations()).containsSequence(station, stationA, stationB);
    }

    @DisplayName("하행 종점구간을 추가한다.")
    @Test
    void addSectionEnd() {
        Station station = new Station("서초역");
        line.addSection(stationB, station, 3);
        assertThat(line.stations()).containsSequence(stationA, stationB, station);
    }

    @DisplayName("여러 구간을 복합적으로 추가한다.")
    @Test
    void addSections() {
        Station station1 = new Station("서초역");
        Station station2 = new Station("논현역");
        Station station3 = new Station("신도림역");
        Station station4 = new Station("신림역");
        line.addSection(stationA, station1, 3); // A 1 B
        line.addSection(station1, station2, 4); // A 1 2 B
        line.addSection(stationB, station3, 4); // A 1 2 B 3
        line.addSection(station4, stationA, 1); // 4 A 1 2 B 3
        assertThat(line.stations()).containsSequence(station4, stationA, station1, station2, stationB, station3);
    }
}
