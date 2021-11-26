package nextstep.subway.section.domain;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_section_to_upStation"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_section_to_downStation"))
    private Station downStation;

    private Distance distance;

    protected Section() {
    }

    public Section(final Station upStation, final Station downStation, final Distance distance) {
        verifySection(upStation, downStation);

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void verifySection(final Station upStation, final Station downStation) {
        if (upStation.isSameStation(downStation)) {
            throw new IllegalArgumentException("같은 역을 구간 설정 할 수 없습니다.");
        }
    }

    public Section withLine(final Line line) {
        this.line = line;
        return this;
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public Distance getDistance() {
        return distance;
    }
}
