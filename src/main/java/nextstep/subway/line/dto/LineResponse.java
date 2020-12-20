package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList<>();
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse(
            Long id, String name, String color, List<StationResponse> stations,
            LocalDateTime createdDate, LocalDateTime modifiedDate
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    // TODO: 향후 사라질 of
    // Line을 조회하는 기능 재구현 시 삭제 예정(해당 기능에서 아래의 Station까지 모두 필요한 내용으로 변경 예정)
    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), null,
                line.getCreatedDate(), line.getModifiedDate());
    }

    public static LineResponse of(Line line, List<Station> stations) {
        List<StationResponse> stationsResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationsResponses,
                line.getCreatedDate(), line.getModifiedDate());
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

    public List<StationResponse> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
