package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {

    private String name;
    private String color;
    private Integer upStationId;
    private Integer downStationId;
    private Integer distance;

    public Line toLine(Station upStation, Station downStation) {
        return Line.of(name, color, upStation, downStation, distance);
    }

    public Integer getUpStationId() {
        return upStationId;
    }

    public Integer getDownStationId() {
        return downStationId;
    }
}
