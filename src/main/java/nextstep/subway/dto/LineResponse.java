package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineResponse {
    private final Long id;

    private final String name;

    private final String color;

    private final Long distance;

    private final StationResponse upStation;

    private final StationResponse downStation;

    public LineResponse(Long id, String name, String color, Long distance, StationResponse upStation, StationResponse downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static LineResponse of(Line line) {
        StationResponse upStationResponse = StationResponse.of(line.getUpStation());
        StationResponse downStationResponse = StationResponse.of(line.getDownStation());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getDistance(), upStationResponse, downStationResponse);
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

    public Long getDistance() {
        return distance;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }
}
