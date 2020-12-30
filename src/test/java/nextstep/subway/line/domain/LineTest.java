package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    public void addSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 잠실역 = new Station("잠실역");
        Line line = new Line("2호선", "green", 강남역, 역삼역, 10L);

        // when
        line.addSection(강남역, 잠실역, 5L);

        // then
        assertThat(line.getStations()).containsExactly(Arrays.array(강남역, 잠실역, 역삼역));
    }
}
