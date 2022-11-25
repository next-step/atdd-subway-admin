package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.time.LocalDateTime;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line.findId(), line.findName(), line.findColor(), line.getCreatedDate(), line.getModifiedDate());
    }
    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
