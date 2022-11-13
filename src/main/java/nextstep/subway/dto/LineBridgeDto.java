package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineBridge;
import nextstep.subway.domain.Station;

public class LineBridgeDto {

    public static class Request {
        private Long upStationId;
        private Long downStationId;
        private int distance;

        public Request() {
        }

        private Request(Long upStationId, Long downStationId, int distance) {
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        public static Request of(Long upStationId, Long downStationId, int distance) {
            return new Request(upStationId, downStationId, distance);
        }

        public Long getDownStationId() {
            return downStationId;
        }

        public Long getUpStationId() {
            return upStationId;
        }

        public int getDistance() {
            return distance;
        }

    }

    public static class Response {
        private Long id;
        private Long upStationId;
        private Long downStationId;
        private int distance;

        public Response() {
        }

        private Response(Long upStationId, Long downStationId, int distance) {
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        public static Response of(LineBridge section) {
            return new Response(section.getUpStation().getId(),section.getDownStation().getId(), section.getDistance());
        }

        public Long getId() {
            return id;
        }

        public int getDistance() {
            return distance;
        }
    }
}
