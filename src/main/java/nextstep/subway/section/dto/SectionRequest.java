package nextstep.subway.section.dto;

public class SectionRequest {

    private Long upStationId;       // 상행역 아이디
    private Long downStationId;     // 하행역 아이디
    private int distance;           // 거리

    public SectionRequest() {

    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public static SectionRequestBuilder builder() {
        return new SectionRequestBuilder();
    }

    public static class SectionRequestBuilder {

        private Long upStationId;
        private Long downStationId;
        private int distance;

        public SectionRequest build() {
            return new SectionRequest(this.upStationId, this.downStationId, this.distance);
        }

        public SectionRequestBuilder upStationId(Long upStationId) {
            this.upStationId = upStationId;
            return this;
        }

        public SectionRequestBuilder downStationId(Long downStationId) {
            this.downStationId = downStationId;
            return this;
        }

        public SectionRequestBuilder distance(int distance) {
            this.distance = distance;
            return this;
        }

    }

}
