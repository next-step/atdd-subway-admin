package nextstep.subway.dto;

import java.time.LocalDateTime;
import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getCreatedDate(), line.getModifiedDate());
    }

    public LineResponse() {

    }

    public LineResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
