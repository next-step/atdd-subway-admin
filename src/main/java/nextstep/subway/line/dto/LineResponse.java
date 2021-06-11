package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.time.LocalDateTime;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse() { }

    public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.id(), line.name(), line.color(), line.getCreatedDate(), line.getModifiedDate());
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public LocalDateTime createdDate() {
        return createdDate;
    }

    public LocalDateTime modifiedDate() {
        return modifiedDate;
    }
}
