package nextstep.subway.section.domain;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static nextstep.subway.common.ErrorCode.*;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.ServiceException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_up_station_id"))
    private Station upStationId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_down_station_id"))
    private Station downStationId;

    @Embedded
    private Distance distance = new Distance();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    public Section(Long upStationId, Long downStationId) {

        if (Objects.isNull(upStationId)) {
            throw new ServiceException(INPUT_INVALID_ERROR, "상행 값이 비어있습니다.");
        }

        if (Objects.isNull(downStationId)) {
            throw new ServiceException(INPUT_INVALID_ERROR, "하행 값이 비어있습니다.");
        }

        if (upStationId.equals(downStationId)) {
            throw new ServiceException(INPUT_INVALID_ERROR, "상행선과 하행선은 동일할 수 없습니다.");
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

    public void setLine(Line line) {
        this.line = line;
    }

    public Station getUpStationId() {
        return upStationId;
    }

    public Station getDownStationId() {
        return downStationId;
    }

    public Distance getDistance() {
        return distance;
    }
}
