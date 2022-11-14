package nextstep.subway.dto;

import javax.persistence.EntityManager;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public Line toLine(EntityManager entityManager) {
        Station upStation = entityManager.getReference(Station.class, upStationId);
        Station downStation = entityManager.getReference(Station.class, downStationId);

        return new Line(name, color, upStation, downStation, distance);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
