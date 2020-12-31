package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Line line = new Line("2호선", "green", 강남역, 잠실역, 10);

        // when
        line.addSection(잠실역, 역삼역, 10);

        // then
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 잠실역, 역삼역));
    }
}
