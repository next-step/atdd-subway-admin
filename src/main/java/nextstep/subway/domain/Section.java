package nextstep.subway.domain;

import nextstep.subway.dto.SectionRequest;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    protected Section() {
    }

    public Section(Long upStationId, Long downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(SectionRequest request) {
        this(request.getUpStationId(), request.getDownStationId(), request.getDistance());
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public void updateUpStationId(Long stationId) {
        this.upStationId = stationId;
    }

    public void updateDownStationId(Long stationId) {
        this.downStationId = stationId;
    }

    public void calculateDistance(Integer distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 작은 값만 등록 가능합니다.");
        }
        this.distance = this.distance - distance;
    }

    public boolean sameUpStation(Long targetId) {
        return this.upStationId.equals(targetId);
    }

    public boolean sameDownStation(Long targetId) {
        return this.downStationId.equals(targetId);
    }
}
