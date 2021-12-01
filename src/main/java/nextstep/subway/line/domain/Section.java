package nextstep.subway.line.domain;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.NoResultDataException;
import nextstep.subway.line.exception.DuplicateSectionStationException;
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

    @Embedded
    private Distance distance = new Distance();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    private Section(Station upStation, Station downStation) {

        if (Objects.isNull(upStation)) {
            throw new NoResultDataException();
        }

        if (upStation.equalsName(downStation)) {
            throw new DuplicateSectionStationException();
        }
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this(upStation, downStation);
        this.distance = distance;
    }

    public Section(Long id, Station upStation, Station downStation, Distance distance, Line line) {
        this(upStation, downStation, distance);
        this.id = id;
        this.line = line;
    }



    protected Section() {
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void changeUpSection(Section newSection) {
        if (this.upStation.equalsName(newSection.getDownStation())) {
            return;
        }
        this.upStation = newSection.getDownStation();
        this.distance = distance.subtract(newSection.getDistance());
    }

    public void changeDownSection(Section newSection) {
        this.downStation = newSection.getUpStation();
        this.distance = distance.subtract(newSection.getDistance());
    }

    public boolean isStations(Section newSection) {
        return (downStation.equalsName(newSection.getDownStation()) && upStation.equalsName(newSection.getUpStation()) ||
            (downStation.equalsName(newSection.getUpStation())) && upStation.equalsName(newSection.getDownStation()));
    }

    public boolean contains(Section newSection) {
        return (downStation.equalsName(newSection.getUpStation()) || upStation.equalsName(newSection.getUpStation()) ||
            (downStation.equalsName(newSection.getDownStation()) || upStation.equalsName(newSection.getDownStation())));
    }

    public boolean isUpStation(Station upStation) {
        return this.upStation.equalsName(upStation);
    }

    public boolean isDowStation(Station downStation) {
        return this.downStation.equalsName(downStation);
    }

    public boolean equalsUpStationName(Station upStation) {
        return this.getDownStation().equalsName(upStation);
    }

    public void removeSection(Section section) {
        this.downStation = section.getDownStation();
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

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
