package nextstep.subway.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private long distance;
    private List<StationResponse> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse(Long id, String name, String color, Long upStation, Long downStation,
        List<StationResponse> list, Long distance, LocalDateTime createdDate,
        LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStation;
        this.downStationId = downStation;
        this.stations = list;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        StationResponse upStation = StationResponse.of(line.getUpStation());
        StationResponse downStation = StationResponse.of(line.getDownStation());
        List<StationResponse> stations = Arrays.asList(upStation, downStation);

        return new LineResponse(line.getId(), line.getName(), line.getColor(), upStation.getId(),
            downStation.getId(), stations, line.getDistance(), line.getCreatedDate(),
            line.getModifiedDate());
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

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
