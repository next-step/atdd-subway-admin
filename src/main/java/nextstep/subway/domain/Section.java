package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public boolean hasSameUpStationAs(Section newSection) {
        return Objects.equals(this.upStation, newSection.upStation);
    }

    public boolean hasSameDownStationAs(Section newSection) {
        return Objects.equals(this.downStation, newSection.downStation);
    }

    public void updateUpStationToDownStationOf(Section newSection) {
        this.upStation = newSection.downStation;

        if (isLastSection()) {
            return;
        }

        this.distance -= newSection.distance;
    }

    public void updateDownStationToUpStationOf(Section newSection) {
        this.downStation = newSection.upStation;

        if (isFirstSection()) {
            return;
        }

        this.distance -= newSection.distance;
    }

    public boolean isFirstSection() {
        return Objects.isNull(upStation);
    }

    public boolean isLastSection() {
        return Objects.isNull(downStation);
    }

    public boolean isNextSectionOf(Section section) {
        return Objects.equals(this.upStation, section.downStation);
    }

    public boolean canInsert(Section newSection) {
        return distance > newSection.distance;
    }

    public boolean equalsAtLeastOneStation(Section newSection) {
        return newSection.upStation.equals(downStation) ||
                newSection.downStation.equals(upStation);
    }

    public Section generateFirstSection() {
        Section firstSection = new Section(null, upStation, 0);
        firstSection.line = line;

        return firstSection;
    }

    public Section generateLastSection() {
        Section lastSection = new Section(downStation, null, 0);
        lastSection.line = line;

        return lastSection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance &&
                Objects.equals(id, section.id) &&
                Objects.equals(upStation, section.upStation) &&
                Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
