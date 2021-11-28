package nextstep.subway.line;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class LineStationCollection {

    private final Line line;
    private final List<Station> stations;

    private LineStationCollection(Line line, List<Station> stations) {
        this.line = line;
        this.stations = stations;
    }

    public static LineStationCollection of(Line line, List<Station> stations) {
        return new LineStationCollection(line, stations);
    }

    public List<LineStationResponse> getLineStationResponses() {
        List<LineStationResponse> result = new ArrayList<>();
        for (LineStation lineStation : line.getStations()) {
            StationResponse stationResponse = StationResponse.of(
                getStationById(lineStation.getStationId()));
            LineStationResponse lineStationResponse = LineStationResponse.of(stationResponse,
                lineStation.getDistanceValue());
            result.add(lineStationResponse);
        }

        return result;
    }

    private Station getStationById(Long stationId) {
        return stations.stream()
            .filter(it -> Objects.equals(it.getId(), stationId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("지하철역이 존재하지 않습니다."));
    }
}
