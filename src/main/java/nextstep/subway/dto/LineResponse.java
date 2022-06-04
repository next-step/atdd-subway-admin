package nextstep.subway.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private Integer distance;
    private List<StationResponse> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static LineResponse from(Line line) {
        List<StationResponse> stations = new ArrayList<>();
        stations.add(StationResponse.from(line.getUpStation()));
        stations.add(StationResponse.from(line.getDownStation()));

        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getDistance(), stations,
                line.getCreatedDate(), line.getModifiedDate());
    }

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, Integer distance, List<StationResponse> stations,
                        LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
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
