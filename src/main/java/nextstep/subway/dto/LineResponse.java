package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;

import java.util.Arrays;
import java.util.List;

public class LineResponse {

    private Long id;

    private String name;

    private String color;

    private List<StationResponse> stations;

    private LineResponse() {

    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                null // TODO: 구간은 리스트로 들어올 수 있어야함(구간을 추가한 경우 추가됨)
//                Arrays.asList(StationResponse.of(line.getUpStation()), StationResponse.of(line.getDownStation()))
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
