package nextstep.subway.section;

import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    private int sequence = 1;

    protected Section() { }

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void toLine(Line line) {
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

    public void increaseSequence() {
        this.sequence += 1;
    }

    public void increaseSequence(int add) {
        this.sequence = add + 1;
    }

    public int getSequence() {
        return sequence;
    }

    public boolean isExtendDownStation(Section section) {
        return this.downStation.equals(section.getUpStation());
    }

    public boolean isExtendUpStation(Section section) {
        return this.upStation.equals(section.getDownStation());
    }

    public boolean isEqualUpStation(Section section) {
        return this.upStation.equals(section.getUpStation());
    }

    public boolean isEqualDownStation(Section section) {
        return this.downStation.equals(section.getDownStation());
    }

    public void replace(Section newSection, Distance totalSectionDistance) {
        Distance excludeSectionDistance = totalSectionDistance.subtract(distance);
        newSection.distance = newSection.distance.subtract(excludeSectionDistance);
        newSection.upStation = upStation;
        newSection.sequence = sequence;
        upStation = newSection.downStation;
        distance = distance.subtract(newSection.distance);
    }

    public void syncUpStation(Section other, Distance distance) {
        this.upStation = other.upStation;
        this.distance = new Distance(other.distance).add(distance);
        this.sequence = other.sequence;
        other.upStation = this.downStation;
        other.distance = other.distance.subtract(this.distance);
    }

    public void syncDownStation(Section other, Distance distance) {
        this.downStation = other.downStation;
        this.distance = new Distance(other.distance).add(distance);
        this.sequence = other.sequence + 1;
        other.downStation = this.upStation;
        other.distance = other.distance.subtract(this.distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", sequence=" + sequence +
                ", upStation=" + upStation.getName() +
                ", downStation=" + downStation.getName() +
                ", distance=" + distance.get() +
                '}';
    }
}
