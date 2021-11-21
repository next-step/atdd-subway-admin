package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.time.LocalDateTime;
import java.util.Objects;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
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

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public boolean equals(Object target) {
        if (this == target) {
            return true;
        }

        if (target == null || getClass() != target.getClass()) {
            return false;
        }

        LineResponse that = (LineResponse) target;

        if (!Objects.equals(id, that.id)) {
            return false;
        }

        if (!Objects.equals(name, that.name)) {
            return false;
        }

        if (!Objects.equals(color, that.color)) {
            return false;
        }

        if (!Objects.equals(createdDate, that.createdDate)) {
            return false;
        }

        return Objects.equals(modifiedDate, that.modifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdDate, modifiedDate);
    }
}
