package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @ManyToOne
    private Station upStation;
    @ManyToOne
    private Station downStation;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation) {
        validate(line, upStation);

        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validate(Line line, Station upStation) {
        if (Objects.isNull(line)) {
            throw new IllegalStateException("Line은 필수입니다.");
        }
        if (Objects.isNull(upStation)) {
            throw new IllegalStateException("upStation은 필수입니다.");
        }
        if (Objects.isNull(line)) {
            throw new IllegalStateException("downStation은 필수입니다.");
        }
    }

}
