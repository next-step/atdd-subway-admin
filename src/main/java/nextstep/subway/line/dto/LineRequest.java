package nextstep.subway.line.dto;

import lombok.Builder;
import nextstep.subway.line.domain.Line;


@Builder
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downstationId;
    private Long distance;

//    private LineRequest() {
//    }
//
//    private LineRequest(String name, String color) {
//        this.name = name;
//        this.color = color;
//    }

//    public static LineRequest of(String name, String color) {
//        return new LineRequest(name,color);
//    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
