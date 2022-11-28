package nextstep.subway.dto;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;

import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList<>();

    public static LineResponse of(Line line) {
        LineResponse response = new LineResponse();
        response.id = line.getId();
        response.name = line.getName();
        response.color = line.getColor();
        response.stations = line.getLineStations().getStationsInOrder()
            .stream().map(lineStation -> StationResponse.of(lineStation.getDownLineStation())).collect(toList());
        return response;
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
}
