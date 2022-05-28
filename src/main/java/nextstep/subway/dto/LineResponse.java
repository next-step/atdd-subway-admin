package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.Arrays;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        Station upStation = line.getUpStation();
        Station downStation = line.getDownStation();
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                Arrays.asList(
                        new StationResponse(upStation.getId(), upStation.getName()),
                        new StationResponse(downStation.getId(), downStation.getName())
                ));
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
