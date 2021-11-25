package nextstep.subway.line.dto;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import nextstep.subway.line.domain.Line;

import java.time.LocalDateTime;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<LineStationResponse> stations = new ArrayList<>();
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, LocalDateTime createdDate,
        LocalDateTime modifiedDate, List<LineStationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
            line.getCreatedDate(), line.getModifiedDate(), Lists.newArrayList());
    }

    public static LineResponse of(Line line, List<LineStationResponse> stations) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
            line.getCreatedDate(), line.getModifiedDate(), stations);
    }

    @Deprecated
    public static LineResponse withOutStationsOf(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
            line.getCreatedDate(), line.getModifiedDate(), Lists.newArrayList());
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

    public List<LineStationResponse> getStations() {
        return stations;
    }
}
