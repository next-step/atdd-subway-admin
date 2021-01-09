package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;       // 상행역 아이디
    private Long downStationId;     // 하행역 아이디
    private int distance;           // 거리

    public LineRequest() {

    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this(name, color);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public static LineRequestBuilder builder() {
        return new LineRequestBuilder();
    }

    public static class LineRequestBuilder {

        private String name;
        private String color;
        private Long upStationId;
        private Long downStationId;
        private int distance;

        public LineRequest build() {
            return new LineRequest(this.name, this.color, this.upStationId, this.downStationId, this.distance);
        }

        public LineRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public LineRequestBuilder color(String color) {
            this.color = color;
            return this;
        }

        public LineRequestBuilder upStationId(Long upStationId) {
            this.upStationId = upStationId;
            return this;
        }

        public LineRequestBuilder downStationId(Long downStationId) {
            this.downStationId = downStationId;
            return this;
        }

        public LineRequestBuilder distance(int distance) {
            this.distance = distance;
            return this;
        }

    }

}
