package nextstep.subway.line.dto;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<LineEndStationResponse> stations;

    private LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private LineResponse(Long id, String name, String color, List<LineEndStationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public static LineResponse from(Line line, Station upStation, Station downStation) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                Lists.newArrayList(
                        LineEndStationResponse.of(upStation.getId(), upStation.getName()),
                        LineEndStationResponse.of(downStation.getId(), downStation.getName())
                )
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

    public List<LineEndStationResponse> getStations() {
        return stations;
    }
}
