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

    public boolean sameStation(Section target, SectionExistType existType) {
        if (existType.equals(SectionExistType.UP_STATION)) {
            return compareStationId(this.upStationId, target.getUpStationId());
        }

        if (existType.equals(SectionExistType.DOWN_STATION)) {
            return compareStationId(this.downStationId, target.getDownStationId());
        }
        return false;
    }

    public boolean sameStationId(Long targetId, SectionExistType existType) {
        if (existType.equals(SectionExistType.UP_STATION)) {
            return compareStationId(this.upStationId, targetId);
        }

        if (existType.equals(SectionExistType.DOWN_STATION)) {
            return compareStationId(this.downStationId, targetId);
        }
        return false;
    }

    public void updateExistOf(Section target, SectionExistType existType) {
        if (existType.equals(SectionExistType.UP_STATION)) {
            this.upStationId = target.getDownStationId();
        }

        if (existType.equals(SectionExistType.DOWN_STATION)) {
            this.downStationId = target.getUpStationId();
        }
        this.relocationDistance(target.getDistance());
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

    private boolean compareStationId(Long stationId, Long targetId) {
        return stationId.equals(targetId);
    }

    private void relocationDistance(Integer distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 작은 값만 등록 가능합니다.");
        }
        this.distance = this.distance - distance;
    }
}
