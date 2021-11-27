package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LineFindResponse {

    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<StationResponse> stations = new ArrayList<>();

    public LineFindResponse() {
    }

    private LineFindResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stations = stations;
    }

    public static LineFindResponse of(Line line) {
        List<Station> stations = line.orderedStations();
        List<StationResponse> stationResponses = new ArrayList<>();
        for (Station station : stations) {
            stationResponses.add(StationResponse.of(station));
        }
        return new LineFindResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate(), stationResponses);
    }

    public static List<LineFindResponse> ofList(List<Line> lines) {
        List<LineFindResponse> lineFindResponses = new ArrayList<>();
        for (Line line : lines) {
            lineFindResponses.add(of(line));
        }
        return lineFindResponses;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
