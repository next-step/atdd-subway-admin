package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import nextstep.subway.line.domain.Line;

public class LineResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public LineResponse(
        final Long id,
        final String name,
        final String color,
        final LocalDateTime createdDate,
        final LocalDateTime modifiedDate
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(final Line line) {
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            line.getCreatedDate(),
            line.getModifiedDate()
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
