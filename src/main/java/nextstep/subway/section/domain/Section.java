package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Table(name = "section")
@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(optional = false)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(upStation, downStation, Distance.from(distance));
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public int getDistance() {
        return distance.getDistance();
    }

    public List<Station> getUpAndDownStation() {
        return Stream.of(upStation, downStation)
                .collect(toList());
    }

    public boolean isSameEdges(Section other) {
        return this.upStation.equals(other.getUpStation()) && this.downStation.equals(other.getDownStation());
    }

    public boolean isSameDownStation(Station otherDownStation) {
        return this.downStation.equals(otherDownStation);
    }

    public boolean isSameUpStation(Station otherUpStation) {
        return this.upStation.equals(otherUpStation);
    }

    public boolean isAfter(Section other) {
        return this.upStation.equals(other.getDownStation());
    }

    public boolean isBefore(Section other) {
        return this.downStation.equals(other.getUpStation());
    }

    public void updateSectionStationByAddNewSection(Section newSection) {
        if (isSameUpStation(newSection.getUpStation())) {
            updateUpStation(newSection);
        }
        if (isSameDownStation(newSection.getDownStation())) {
            updateDownStation(newSection);
        }
    }

    private void updateDownStation(Section newSection) {
        changeDistanceByNewSectionDistance(newSection.distance);
        this.downStation = newSection.upStation;
    }

    private void updateUpStation(Section newSection) {
        changeDistanceByNewSectionDistance(newSection.distance);
        this.upStation = newSection.downStation;
    }

    private void changeDistanceByNewSectionDistance(Distance newSectionDistance) {
        this.distance = distance.distanceDiffWithOtherDistance(newSectionDistance);
    }

    public Section combineWithDownSection(Section downSection) {
        Section combinedSection = new Section(this.upStation, downSection.getDownStation(), this.distance.plus(downSection.getDistance()));
        combinedSection.setLine(this.line);
        return combinedSection;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }
}
