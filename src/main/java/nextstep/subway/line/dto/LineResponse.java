package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;
//    private Long upStationId;
//    private Long downStationId;
//    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color,
//                        Long upStationId, Long downStationId, int distance,
                        List<Station> stations,
                        LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
//        this.upStationId = upStationId;
//        this.downStationId = downStationId;
//        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
//                line.getUpStationId(), line.getDownStationId(), line.getDistance(),
                null,
                line.getCreatedDate(), line.getModifiedDate());
    }

    public static List<LineResponse> listOf(List<Line> lines) {
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
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

//    public Long getUpStationId() {
//        return upStationId;
//    }
//
//    public Long getDownStationId() {
//        return downStationId;
//    }
//
//    public int getDistance() {
//        return distance;
//    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public List<Station> getStations() {
//        return Arrays.asList(new Station("강남역"), new Station("역삼역"));
        return this.stations;
    }
}
