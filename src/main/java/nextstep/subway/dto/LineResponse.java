package nextstep.subway.dto;

import nextstep.subway.domain.Color;
import nextstep.subway.domain.Name;

import java.util.List;

public class LineResponse {

    private final long id;
    private final Name name;
    private final Color color;
    private final List<StationResponse> stations;

    public LineResponse(long id, Name name, Color color, List<StationResponse> stations) {
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
