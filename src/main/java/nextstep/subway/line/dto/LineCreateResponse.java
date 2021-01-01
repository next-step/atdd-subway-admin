package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;

public class LineCreateResponse {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineCreateResponse() {
    }

    public LineCreateResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public LineCreateResponse(Long id, String name, String color, Long upStationId, Long downStationId, Integer distance,
                              LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineCreateResponse of(Line line) {
        Station upStation = line.getUpStation();
        Station downStation = line.getDownStation();

        Long upStationId = upStation != null ? upStation.getId(): null;
        Long downStationId = downStation != null ? downStation.getId(): null;
        return new LineCreateResponse(
                line.getId(), line.getName(), line.getColor(),
                upStationId, downStationId, line.getDistance(),
                line.getCreatedDate(), line.getModifiedDate());
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

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
