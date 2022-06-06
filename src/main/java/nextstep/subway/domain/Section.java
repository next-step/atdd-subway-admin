package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
    @JoinColumn(nullable = false)
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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
        this.distance -= newSection.distance;
    }

    public void updateDownStationToUpStationOf(Section newSection) {
        this.downStation = newSection.upStation;
        this.distance -= newSection.distance;
    }

    public boolean isFirstSection() {
        return Objects.isNull(upStation);
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean isNextSectionOf(Section section) {
        return Objects.equals(this.upStation, section.downStation);
    }

    public boolean hasSameStations(Section other) {
        return this.upStation == other.upStation && this.downStation == other.downStation;
    }

    public boolean canInsert(Section newSection) {
        return distance > newSection.distance;
    }

    public boolean equalsAtLeastOneStation(Section other) {
        return other.upStation.equals(upStation) ||
                other.upStation.equals(downStation) ||
                other.downStation.equals(upStation) ||
                other.downStation.equals(downStation);
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
