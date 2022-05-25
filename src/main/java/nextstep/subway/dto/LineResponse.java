package nextstep.subway.dto;

import java.time.LocalDateTime;
import nextstep.subway.domain.Line;

public class LineResponse extends BaseDto {
    private Long id;
    private String name;
    private String color;

    public LineResponse() {
        super();
    }

    private LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        super(createdDate, modifiedDate);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate());
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
}
