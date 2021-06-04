package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        upStation = new Station("강남역");
        downStation = new Station("역삼역");
    }

    @Test
    void canCreate() {
        Line line = new Line("2호선", "green");

        assertThat(new Section(line, upStation, downStation, 10)).isNotNull();
    }
}
