package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private int distance;
    private String upStationName;
    private String downStationName;
    private Long upStationId;
    private Long downStationId;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, String upStationName, String downStationName, int distance,
                        Long upStationId, Long downStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStationName = upStationName;
        this.downStationName = downStationName;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getUpStation().getName(),
                line.getDownStation().getName(), line.getDistance(), line.getUpStation().getId(),
                line.getDownStation().getId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
