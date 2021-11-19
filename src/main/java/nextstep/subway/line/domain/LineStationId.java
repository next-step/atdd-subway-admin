package nextstep.subway.line.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;

import nextstep.subway.station.domain.Station;

@Embeddable
public class LineStationId implements Serializable {

    private Long upStationId;

    private Long downStationId;

    public LineStationId(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("상행선과 하행선은 동일할 수 없습니다.");
        }
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    protected LineStationId() {
    }
}
