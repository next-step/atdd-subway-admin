package nextstep.subway.domain;

import nextstep.subway.exception.ErrorStatus;
import nextstep.subway.exception.IllegalRequestBody;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @Column
    private Long distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public boolean isDuplicateSection(Section section) {
        if (this.getUpStation().equals(section.getUpStation()) && this.getDownStation().equals(section.getDownStation())) {
            return true;
        }
        return this.getUpStation().equals(section.downStation) && this.getDownStation().equals(section.upStation);
    }

    public void updateUpStation(Section newSection) {
        validateDistance(newSection);
        this.upStation = newSection.getDownStation();
        this.distance -= newSection.distance;
    }

    public void updateDownStation(Section newSection) {
        validateDistance(newSection);
        this.downStation = newSection.getUpStation();
        this.distance -= newSection.distance;
    }

    public boolean anyMatch(Section section) {
        if (this.downStation.equals(section.getUpStation())) {
            return true;
        }
        if (this.downStation.equals(section.getDownStation())) {
            return true;
        }
        if (this.upStation.equals(section.getDownStation())) {
            return true;
        }
        return this.upStation.equals(section.getUpStation());
    }

    public boolean inCludeStationInSection(Station deleteStation) {
        return this.upStation.equals(deleteStation) || this.downStation.equals(deleteStation);
    }

    public Section merge(Section downStationSection) {
        return new Section(this.line, this.upStation, downStationSection.downStation, this.distance + downStationSection.distance);
    }

    public Long getDistance() {
        return distance;
    }

    public Station getDownStation() {
        return downStation;
    }

    private void validateDistance(Section newSection) {
        if (this.distance <= newSection.distance) {
            throw new IllegalRequestBody(ErrorStatus.DISTANCE_LENGTH.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        if (this.id == null || section.id == null) {
            return false;
        }
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
