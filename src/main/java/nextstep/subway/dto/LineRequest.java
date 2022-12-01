package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {

    private String name;
    private String color;
    private Station upStation;
    private Station downStation;
    private int distance;

    public LineRequest() {

    }

    public LineRequest(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Station getUpStation()
    {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
    public Line toLine() {
        return new Line(name, color, upStation, downStation, distance);
    }

    public void setName(String name) {
        this.name = name;
    }

}
