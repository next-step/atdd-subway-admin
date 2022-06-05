package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class LineStationsTest {
    private final Line line = new Line("신분당선", "bg-red-600");
    private final Station station1 = new Station("강남역");
    private final Station station2 = new Station("양재역");

    @Test
    void 연관관계를_추가할_수_있어야_한다() {
        // given
        final LineStation lineStation = new LineStation(line, station1);
        final LineStations lineStations = new LineStations();

        // when
        lineStations.add(lineStation);

        // then
        assertThat(lineStations.getStations().size()).isEqualTo(1);
    }

    @Test
    void 지하철역_목록을_확인할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when
        final List<Station> stations = lineStations.getStations();

        // then
        assertThat(stations).containsExactly(station1, station2);
    }

    @Test
    void 특정_지하철역의_이전_역을_확인할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when
        final Station previous = lineStations.getPreviousOf(station2);

        // then
        assertThat(previous).isEqualTo(station1);
    }

    private LineStations givenLineStations() {
        final List<LineStation> lineStationList = Arrays.asList(
                new LineStation(line, station1),
                new LineStation(line, station2, station1));
        return new LineStations(lineStationList);
    }
}
