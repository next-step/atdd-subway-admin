package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;

    public LineRequest() {

    }

    public LineRequest(LineRequestBuilder builder) {
        this.name = builder.name;
        this.color = builder.color;
        this.upStationId = builder.upStationId;
        this.downStationId = builder.downStationId;
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
        return new Line(name, color, upStation, downStation);
    }

    public static LineRequest.LineRequestBuilder builder() {
        return new LineRequest.LineRequestBuilder();
    }

    public static class LineRequestBuilder {
        private String name;
        private String color;
        private Long upStationId;
        private Long downStationId;

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

        public LineRequest build() {
            return new LineRequest(this);
        }

    }


}
