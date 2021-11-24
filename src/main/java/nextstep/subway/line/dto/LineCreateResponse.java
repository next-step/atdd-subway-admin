package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineCreateResponse {
    private final Long id;

    public LineCreateResponse(Long id) {
        this.id = id;
    }

    public static LineCreateResponse of(Line line) {
        return new LineCreateResponse(line.getId());
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "LineCreateResponse{" +
                "id=" + id +
                '}';
    }
}
