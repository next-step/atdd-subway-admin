package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

/**
 *  TODO : 일급 컬렉션 관련 수정하기
 */
public class LineRequest {
    private String name;

    private String color;

    private Long upStationId;

    private Long downStationId;

    private Distance distance;

    private LineRequest() {
    }

    private LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = Distance.of(distance);
    }

    public static LineRequest of(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
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


    public Line toLine() {
        return new Line(name, color);
    }
    public Line toLine(Station upStation, Station downStation) {
        Line line = new Line(name, color);
        line.addSection(new Section(distance, upStation, downStation));
        return line;
    }
}
