package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class LineStationsTest {
    @Test
    void getStations_호출_시_지하철역_목록을_반환해야_한다() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final Station station1 = new Station("강남역");
        final Station station2 = new Station("양재역");
        final List<LineStation> lineStationList = new ArrayList<>();
        lineStationList.add(new LineStation(line, station1));
        lineStationList.add(new LineStation(line, station2));
        final LineStations lineStations = new LineStations(lineStationList);

        // when
        final List<Station> stations = lineStations.getStations();

        // then
        assertThat(stations).containsExactly(station1, station2);
    }
}
