package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간에 대한 테스트")
class SectionsTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = LineTest.지하철_1호선_생성됨();
    }

    @DisplayName("지하철 구간의 초기 값을 설정합니다.")
    @Test
    void init() {
        // given
        LineStation lineStation = LineStation.of(line, new Station("청량리역"), new Station("신창역"), 100);

        // when
        LineStations init = LineStations.init(lineStation);

        // then
        assertThat(init).isNotNull();
        assertThat(init.getLineStations()).containsExactly(lineStation);
    }

    @DisplayName("지하철 구간을 추가합니다.")
    @Test
    void add() {
        // given
        LineStation lineStation = LineStation.of(line, new Station("청량리역"), new Station("신창역"), 100);
        LineStations lineStations = LineStations.init(lineStation);
        LineStation newStation = LineStation.of(line, new Station("당정역"), new Station("금정역"), 10);

        // when
        lineStations.add(newStation);

        // then
        assertThat(lineStations.getLineStations()).contains(newStation);
    }

}
