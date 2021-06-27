package nextstep.subway.common;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationResponse;

public class LineTestData {

    private final LineRequest line;
    private final List<StationResponse> stations;

    public LineTestData(String name, String color,
                        StationResponse upStation,
                        StationResponse downStation) {
        this(name, color, 100, upStation, downStation);
    }

    public LineTestData(String name, String color, int distance,
                        StationResponse upStation,
                        StationResponse downStation) {
        this.line = new LineRequest(name, color, upStation.getId(), downStation.getId(), distance);
        this.stations = Arrays.asList(upStation, downStation);
    }

    public LineRequest getLine() {
        return line;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
