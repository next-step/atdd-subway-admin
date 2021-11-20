package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.time.LocalDateTime;

public class LineCreateResponse {
    private Long id;
    private String name;
    private String color;
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

    public static LineCreateResponse of(Line line) {
        return new LineCreateResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate());
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
}
