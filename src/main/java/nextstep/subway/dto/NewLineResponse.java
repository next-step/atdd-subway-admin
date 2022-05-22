package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class NewLineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public NewLineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = toStationResponse(line);
    }

    private List<StationResponse> toStationResponse(Line line) {
        return line.getStations()
                   .stream()
                   .map(StationResponse::of)
                   .collect(Collectors.toList());
    }

    protected NewLineResponse() {
    }
}
