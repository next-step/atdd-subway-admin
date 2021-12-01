package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    @DisplayName("상-하행 순으로 정렬")
    @Test
    void orderStations() {
        //given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Line 신분당선 = new Line("신분당선", "bg-red-600", 강남역, 광교역, 10);

        //when
        List<Station> stations = 신분당선.getStations();

        //then
        assertThat(stations).isEqualTo(Arrays.asList(강남역, 광교역));
    }
}
