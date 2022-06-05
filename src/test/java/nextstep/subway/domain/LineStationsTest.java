package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.Test;

class LineStationsTest {
    private final Line line = new Line("신분당선", "bg-red-600");
    private final Station station1 = new Station("강남역");
    private final Station station2 = new Station("양재역");

    @Test
    void 연관관계를_추가할_수_있어야_한다() {
        // given
        final LineStation lineStation = new LineStation(line, station1, null, station2);
        final LineStations lineStations = new LineStations();

        // when
        lineStations.add(lineStation);

        // then
        assertThat(lineStations.stations().size()).isEqualTo(1);
    }

    @Test
    void 지하철역_목록을_조회할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when
        final List<StationResponse> stations = lineStations.stations();

        // then
        assertThat(stations).containsExactly(StationResponse.of(station1), StationResponse.of(station2));
    }

    @Test
    void 지하철역으로_LineStation_객체를_조회할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when
        final Optional<LineStation> lineStation = lineStations.getByStation(station1);

        // then
        assertThat(lineStation.isPresent()).isTrue();
        assertThat(lineStation.get().getStation()).isEqualTo(station1);
    }

    private LineStations givenLineStations() {
        final List<LineStation> lineStationList = Arrays.asList(
                new LineStation(line, station1, null, station2),
                new LineStation(line, station2, station1, null));
        return new LineStations(lineStationList);
    }
}
