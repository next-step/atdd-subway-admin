package nextstep.subway.line.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Section implements Serializable {

    private Long upStationId;

    private Long downStationId;

    @Embedded
    private Distance distance = new Distance(0);

    public Section(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("상행선과 하행선은 동일할 수 없습니다.");
        }
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Section(Long upStationId, Long downStationId, Distance distance) {
        this(upStationId, downStationId);
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation) {
        this(upStation.getId(), downStation.getId());
    }

    protected Section() {
    }
}
