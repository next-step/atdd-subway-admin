package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionRequest;

public class LineRequest extends SectionRequest {

    private String name;
    private String color;

    public LineRequest() {

    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        super(upStationId, downStationId, distance);
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public static LineRequestBuilder builder() {
        return new LineRequestBuilder();
    }

    public static class LineRequestBuilder extends SectionRequestBuilder {

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
