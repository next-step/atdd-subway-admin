package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public void validSection(Section section) {
        validNotInStations(section);
        validSameStation(section);
        this.distance.validDistance(section.distance);
    }

    private void validNotInStations(Section section) {
        if (!isInStations(section)) {
            throw new SubwayException(ErrorCode.VALID_NOT_IN_STATIONS_ERROR);
        }
    }

    private boolean isInStations(Section compareSection) {
        if (compareSection.upStation != this.upStation && compareSection.downStation == this.upStation) {
            return true;
        }

        if (compareSection.downStation != this.downStation && compareSection.upStation == this.downStation) {
            return true;
        }

        return isSameUpStation(compareSection.upStation) || isSameUpStation(compareSection.downStation);
    }

    public boolean isSameUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isSameDownStation(Station station) {
        return downStation.equals(station);
    }

    private void validSameStation(Section compareSection) {
        if (isSameUpDownStation(compareSection)) {
            throw new SubwayException(ErrorCode.VALID_SAME_STATION_ERROR);
        }
    }

    public boolean isSameUpDownStation(Section compareSection) {
        return isSameUpStation(compareSection.upStation) && isSameDownStation(compareSection.downStation);
    }

    public void resetSection(Section expectSection) {
        if (isSameUpStation(expectSection.upStation)) {
            this.upStation = expectSection.downStation;
        }

        if (isSameDownStation(expectSection.downStation)) {
            this.downStation = expectSection.upStation;
        }

        this.distance.divideDistance(expectSection.distance);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
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

    public void removeSection(Section removeSection) {
        if (isSameUpStation(removeSection.downStation)) {
            this.upStation = removeSection.upStation;
        }

        if (isSameDownStation(removeSection.upStation)) {
            this.downStation = removeSection.downStation;
        }

        this.distance.plusDistance(removeSection.distance);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return getId().equals(section.getId());
    }


    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation, distance);
    }
}
