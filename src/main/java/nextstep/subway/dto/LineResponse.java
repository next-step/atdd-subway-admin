package nextstep.subway.dto;

import nextstep.subway.domain.Color;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Name;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

    private final long id;
    private final Name name;
    private final Color color;
    private final List<StationToLineResponse> stations;

    public LineResponse(long id, Name name, Color color, List<StationToLineResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    public String getColor() {
        return color.getColor();
    }

    public List<StationToLineResponse> getStations() {
        return stations;
    }

    public static LineResponse fromLineStations(Line line, Stations stations) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                stations.makeStationToLineResponse());
    }
}
