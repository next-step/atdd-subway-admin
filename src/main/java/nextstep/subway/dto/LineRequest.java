package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {

    private LineRequest() {}

    public static class Create {
        private String name;
        private String color;
        private Long upStationId;
        private Long downStationId;
        private Long distance;

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }

        public Long getUpStationId() {
            return this.upStationId;
        }

        public Long getDownStationId() {
            return this.downStationId;
        }

        public Long getDistance() {
            return distance;
        }

        public Line toLine(Station upStation, Station downStation) {
            return new Line(name, color, upStation, downStation);
        }
    }

    public static class Modification {
        private String name;
        private String color;

        public Line toLine(Line originalLine) {
            return originalLine.modify(this);
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }
    }
}
