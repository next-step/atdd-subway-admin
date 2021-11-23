package nextstep.subway.line.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Section implements Serializable {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_up_station_id"))
    private Station upStationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_down_station_id"))
    private Station downStationId;

    @Embedded
    private Distance distance = new Distance();

    public Section(Long upStationId, Long downStationId) {

        if (Objects.isNull(upStationId)) {
            throw new IllegalArgumentException("상행 값이 비어있습니다.");
        }

        if (Objects.isNull(downStationId)) {
            throw new IllegalArgumentException("하행 값이 비어있습니다.");
        }

        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("상행선과 하행선은 동일할 수 없습니다.");
        }
        this.upStationId = new Station(upStationId);
        this.downStationId = new Station(downStationId);
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
