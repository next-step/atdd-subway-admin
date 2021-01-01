package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.time.LocalDateTime;

public class LineCreateResponse {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;
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

    public LineCreateResponse(Long id, String name, String color, Long upStationId, Long downStationId, int distance, LocalDateTime createdDate, LocalDateTime modifiedDate) {
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
        return new LineCreateResponse(line.getId(), line.getName(), line.getColor(), 1L, 2L, 10, line.getCreatedDate(), line.getModifiedDate());
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

    public int getDistance() {
        return distance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
