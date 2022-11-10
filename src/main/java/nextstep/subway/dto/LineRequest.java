package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {
    private String name;
    private String color;
    private String upStationName;
    private String downStationName;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toLine() {
        return new Line(name, color,new Station(upStationName),new Station(downStationName));
    }
}
