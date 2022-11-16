package nextstep.subway.dto;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {

    private String name;
    private String color;
    private int distance;
    private Long upStationId;
    private Long downStationId;
    private Station upStation;
    private Station downStation;

    public LineRequest() {
    }

    public LineRequest(String name, String color, int distance, Long upStationId, Long downStationId) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void isUpStationThenSet(List<Station> stations) {
        this.upStation = stations.stream().filter(station -> station.getId().equals(upStationId))
                .findFirst().orElseThrow(EntityNotFoundException::new);
    }

    public void isDownStationThenSet(List<Station> stations) {
        this.downStation = stations.stream()
                .filter(station -> station.getId().equals(downStationId)).findFirst()
                .orElseThrow(EntityNotFoundException::new);
    }
}
