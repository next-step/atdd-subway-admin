package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;

public class LineDto {

    public static class Request{

        private String name;
        private String color;
        private int upStationId;
        private int downStationId;
        private int distance;

        public Line of() {
            return new Line(this);
        }

        public String getName() {
            return name;
        }

        public Request setName(String name) {
            this.name = name;
            return this;
        }

        public String getColor() {
            return color;
        }

        public Request setColor(String color) {
            this.color = color;
            return this;
        }

        public int getUpStationId() {
            return upStationId;
        }

        public Request setUpStationId(int upStationId) {
            this.upStationId = upStationId;
            return this;
        }

        public int getDownStationId() {
            return downStationId;
        }

        public Request setDownStationId(int downStationId) {
            this.downStationId = downStationId;
            return this;
        }

        public int getDistance() {
            return distance;
        }

        public Request setDistance(int distance) {
            this.distance = distance;
            return this;
        }
    }

    public static class Response{
        public int id;
        public String name;
        public String color;
        public List<StationResponse> stations;
        public int distance;
    }
}
