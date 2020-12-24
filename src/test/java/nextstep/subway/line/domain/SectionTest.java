package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간에 대한 테스트")
class SectionTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = LineTest.지하철_1호선_생성됨();
    }

    @DisplayName("지하철 구간을 생성합니다.")
    @Test
    void create() {
        // given
        Station station1 = new Station("금정역");
        Station station2 = new Station("당정역");
        int distance = 100;

        // when
        LineStation lineStation = LineStation.of(line, station1, station2, distance);

        // then
        assertThat(lineStation).isNotNull();
    }

    @DisplayName("지하철 구간에 같은 노선과 같은 역이라면 동등성을 보장합니다.")
    @Test
    void equals() {
        // given
        Station station1 = new Station("금정역");
        Station station2 = new Station("당정역");
        LineStation lineStation1 = LineStation.of(line, station1, station2, 100);
        LineStation lineStation2 = LineStation.of(line, station1, station2, 200);

        // when
        boolean equals = lineStation1.equals(lineStation2);

        // then
        assertThat(equals).isTrue();
    }

    @DisplayName("지하철 구간에 같은 노선과 같은 역이 아니라면 동등성을 보장하지 않습니다.")
    @Test
    void equalsNot() {
        // given
        Station station1 = new Station("금정역");
        Station station2 = new Station("당정역");
        Station station3 = new Station("관악역");
        LineStation lineStation1 = LineStation.of(line, station1, station2, 100);
        LineStation lineStation2 = LineStation.of(line, station1, station3, 100);

        // when
        boolean equals = lineStation1.equals(lineStation2);

        // then
        assertThat(equals).isFalse();
    }
}
