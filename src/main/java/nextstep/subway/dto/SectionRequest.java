package nextstep.subway.dto;

public class SectionRequest {
    private Long upStation_id;
    private Long downStation_id;
    private Integer distance;

    public SectionRequest(Long upStation_id, Long downStation_id, Integer distance) {
        this.upStation_id = upStation_id;
        this.downStation_id = downStation_id;
        this.distance = distance;
    }

    public Long getUpStation_id() {
        return upStation_id;
    }

    public Long getDownStation_id() {
        return downStation_id;
    }

    public Integer getDistance() {
        return distance;
    }
}
