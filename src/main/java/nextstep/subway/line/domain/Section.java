package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Section {
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public Section() {
    }

    public Section(Long upStationId, Long downStationId, Integer distance) {
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
}
