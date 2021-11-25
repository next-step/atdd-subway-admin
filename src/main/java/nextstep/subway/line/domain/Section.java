package nextstep.subway.line.domain;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import java.util.Objects;
import java.util.function.Predicate;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.NoResultDataException;
import nextstep.subway.line.exception.DuplicationSectionStationException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_station"))
    private Station upStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_next_station"))
    private Station downStation;

    @Enumerated(EnumType.STRING)
    private SectionType sectionType;

    @Embedded
    private Distance distance = new Distance();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    public Section(Station upStation, Station downStation) {

        if (Objects.isNull(upStation)) {
            throw new NoResultDataException();
        }

        if (upStation.equals(downStation)) {
            throw new DuplicationSectionStationException();
        }
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Section(Station upStation, Station downStation, SectionType sectionType, Distance distance) {
        this(upStation, downStation);
        this.sectionType = sectionType;
        this.distance = distance;
    }

    protected Section() {
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public SectionType getSectionType() {
        return sectionType;
    }

    public boolean equalsNextSection(Station downStation) {
        return upStation.equals(downStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation)
            && Objects.equals(downStation, section.downStation) && sectionType == section.sectionType
            && Objects.equals(distance, section.distance) && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, sectionType, distance, line);
    }
}
