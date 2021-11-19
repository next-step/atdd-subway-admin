package nextstep.subway.line.application.dto;

import nextstep.subway.line.dto.LineRequest;

public class LineUpdateRequest {
    private Long id;
    private String name;
    private String color;

    public LineUpdateRequest(Long id, LineRequest lineRequest) {
        this.id = id;
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
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
