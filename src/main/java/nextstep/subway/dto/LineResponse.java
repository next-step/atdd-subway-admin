package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private Long distance;
    private List<Station> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, Long distance, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.stations = stations;
    }

    public static LineResponse of(Line save) {
        Station upStation = Station.of(save.getUpStation());
        Station downStation = Station.of(save.getDownStation());
        List<Station> stations = Arrays.asList(upStation, downStation);
        return new LineResponse(save.getId(),save.getName(),save.getColor(),save.getDistance(),stations);
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

    public Long getDistance() {
        return distance;
    }
}

