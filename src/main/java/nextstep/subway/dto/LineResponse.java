package nextstep.subway.dto;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineResponse {
    private String name;

    private String color;

    private List<Station> stations;

    public static LineResponse of(final Line line) {
        return new LineResponse(line.getName(),
                line.getColor(),
                line.getLineStations().getStations());
    }

    public LineResponse() {
    }

    public LineResponse(final String name, final String color, final List<Station> stations) {
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations;
    }
}
