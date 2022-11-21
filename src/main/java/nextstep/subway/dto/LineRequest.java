package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class LineRequest {

    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private long distance;

    public LineRequest() {

    }

    public LineRequest(LineRequestBuilder builder) {
        this.name = builder.name;
        this.color = builder.color;
        this.distance = builder.distance;
        this.upStationId = builder.upStationId;
        this.downStationId = builder.downStationId;
    }

    public long getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public Line toLine(Station upStation, Station downStation) {
        Line line = new Line(name,color);
        Section section = new Section(line,upStation,downStation,distance);
        line.addSection(section);
        return line;
    }

    public static LineRequest.LineRequestBuilder builder() {
        return new LineRequest.LineRequestBuilder();
    }

    public static class LineRequestBuilder {
        private String name;
        private String color;
        private Long upStationId;
        private Long downStationId;
        private long distance;

        public LineRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public LineRequestBuilder color(String color) {
            this.color = color;
            return this;
        }

        public LineRequestBuilder distance(long distance) {
            this.distance = distance;
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

        public LineRequest build() {
            return new LineRequest(this);
        }

    }
}
