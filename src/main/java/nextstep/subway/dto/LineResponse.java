package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineResponse {
    private final Long id;

    private final String name;

    private final String color;

    private final List<Station> stations;

    public LineResponse(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<Station> stationList = new ArrayList<>();

        if (line.hasUpStation()) {
            stationList.add(line.getUpStation());
        }

        if (line.hasDownStation()) {
            stationList.add(line.getDownStation());
        }

        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                stationList
        );
    }

    public Long getId() {
        return id;
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
