package nextstep.subway.line.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    protected Section() {
    }

    public Section(final Station upStation, final Station downStation) {
        verifySection(upStation, downStation);

        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void verifySection(final Station upStation, final Station downStation) {
        if (upStation.isSameStation(downStation)) {
            throw new IllegalArgumentException("같은 역을 구간 설정 할 수 없습니다.");
        }
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }
}
