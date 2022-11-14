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
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, Long distance, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.stations = stations;
    }

    public static LineResponse of(Line save) {
        StationResponse upStation = StationResponse.of(save.getUpStation());
        StationResponse downStation = StationResponse.of(save.getDownStation());
        List<StationResponse> stations = Arrays.asList(upStation, downStation);
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

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}

