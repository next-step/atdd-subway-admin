package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

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
        List<Station> stations = new ArrayList<>();
        line.getSections().forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });

        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                stations
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
