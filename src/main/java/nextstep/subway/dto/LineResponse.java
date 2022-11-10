package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList<>();

    public static LineResponse of(Line line, Station upStation, Station downStation) {
        LineResponse response = new LineResponse();
        response.id = line.getId();
        response.name = line.getName();
        response.color = line.getColor();
        response.stations.add(new StationResponse(upStation.getId(), upStation.getName()));
        response.stations.add(new StationResponse(downStation.getId(), downStation.getName()));
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
