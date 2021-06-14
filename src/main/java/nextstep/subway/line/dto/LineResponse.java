package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<StationResponse> stationResponse;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<Section> sections, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationResponse = getStationResponse(sections);
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    private List<StationResponse> getStationResponse(List<Section> sections) {
        List<StationResponse> stationResponses = new ArrayList<>();
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            stationResponses.add(StationResponse.of(upStation));
            stationResponses.add(StationResponse.of(downStation));
        }
        return stationResponses;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getSections(), line.getCreatedDate(), line.getModifiedDate());
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

    public List<StationResponse> getStationResponse() {
        return stationResponse;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
