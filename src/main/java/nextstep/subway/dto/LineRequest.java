package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {
    private String name;
    private String color;
    private String upStationName;
    private String downStationName;

    public LineRequest(){

    }
    public LineRequest(LineRequestBuilder builder) {
        this.name = builder.name;
        this.color = builder.color;
        this.upStationName = builder.upStationName;
        this.downStationName = builder.downStationName;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getUpStationName() {
        return upStationName;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public Line toLine() {
        return new Line(name, color, new Station(upStationName), new Station(downStationName));
    }

    public static LineRequest.LineRequestBuilder builder() {
        return new LineRequest.LineRequestBuilder();
    }

    public static class LineRequestBuilder {
        private String name;
        private String color;
        private String upStationName;
        private String downStationName;

        public LineRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public LineRequestBuilder color(String color) {
            this.color = color;
            return this;
        }

        public LineRequestBuilder upStationName(String upStationName) {
            this.upStationName = upStationName;
            return this;
        }

        public LineRequestBuilder downStationName(String downStationName) {
            this.downStationName = downStationName;
            return this;
        }

        public LineRequest build() {
            return new LineRequest(this);
        }

    }


}
