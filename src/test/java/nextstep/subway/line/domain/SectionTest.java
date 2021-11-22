package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    private Line line;
    private Station station;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        station = new Station("강남역");
    }

    @DisplayName("지하철 구간 생성시 두 종정역이 같은지 검사한다.")
    @Test
    void validateStation() {
        Line line = new Line("2호선", "green");
        Station station = new Station("강남역");
        assertThatThrownBy(() -> {
            new Section(line, station, station, 10);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 생성시 거리가 0보다 큰 지 검사한다.")
    @Test
    void validateDistance() {
        assertThatThrownBy(() -> {
            new Section(line, station, new Station("역삼역"), 0);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
