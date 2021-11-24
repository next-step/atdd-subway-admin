package nextstep.subway.line.domain;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import java.util.Objects;

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
import nextstep.subway.line.exception.DuplicationStationException;
import nextstep.subway.common.exception.NoResultDataException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity implements Comparable<Section> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private SectionType sectionType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_station"))
    private Station station;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_next_station"))
    private Station nextStation;

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
            throw new DuplicationStationException();
        }
        this.station = upStation;
        this.nextStation = downStation;
    }

    public Section(Station station, Station nextStation, SectionType sectionType, Distance distance) {
        this(station, nextStation);
        this.sectionType = sectionType;
        this.distance = distance;
    }

    protected Section() {
    }

    public void setLine(Line line) {
        this.line = line;
    }

    @Override
    public int compareTo(Section target) {

        if (isUpStation()) {
            return -1;
        }

        if (isNotExistsNextStationOrEqualsNextStation(target) ) {
            return 1;
        }

        if (isNotExistsTargetNextStation(target)) {
            return -1;
        }

        return 0;
    }

    private boolean isUpStation() {
        return this.sectionType.equals(SectionType.UP);
    }

    private boolean isNotExistsNextStationOrEqualsNextStation(Section target) {
        return (this.nextStation == null || this.nextStation.equals(target.station));
    }

    private boolean isNotExistsTargetNextStation(Section target) {
        return target.nextStation == null ;
    }


    public SectionType getSectionType() {
        return sectionType;
    }

    public Station getStation() {
        return station;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Distance getDistance() {
        return distance;
    }
}
