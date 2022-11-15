package nextstep.subway.domain;

import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Integer distance) {
        this(line, upStation, downStation, Distance.valueOf(distance));
    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public boolean isUpStation(Station otherUpStation) {
        return this.upStation.equals(otherUpStation);
    }

    public boolean isDownStation(Station otherDownStation) {
        return downStation.equals(otherDownStation);
    }

    private boolean canSplitByDownStation(Station otherUpStation, Station otherDownStation) {
        return !isUpStation(otherUpStation) && isDownStation(otherDownStation);
    }

    private boolean canSplitByUpStation(Station otherUpStation, Station otherDownStation) {
        return isUpStation(otherUpStation) && !isDownStation(otherDownStation);
    }

    public List<Section> splitSection(Station upStation, Station downStation, Distance distance) {
        if (canSplitByUpStation(upStation, downStation)) {
            return splitSectionByUpStation(downStation, distance);
        }
        if (canSplitByDownStation(upStation, downStation)) {
            return splitSectionByDownStation(upStation, distance);
        }
        if (canAddOnUpStation(downStation)) {
            return addOnUpStation(upStation, distance);
        }
        if (canAddOnDownStation(upStation)) {
            return addOnDownStation(downStation, distance);
        }
        return Collections.singletonList(this);
    }

    private List<Section> addOnUpStation(Station appendedUpStation, Distance distance) {
        return Lists.newArrayList(
            new Section(line, appendedUpStation, this.upStation, distance),
            this
        );
    }

    private List<Section> addOnDownStation(Station appendedDownStation, Distance distance) {
        return Lists.newArrayList(
                this,
            new Section(line, this.downStation, appendedDownStation, distance)
        );
    }

    private boolean canAddOnUpStation(Station downStation) {
        return upStation.equals(downStation);
    }

    private boolean canAddOnDownStation(Station upStation) {
        return downStation.equals(upStation);
    }

    private List<Section> splitSectionByUpStation(Station appendedStation, Distance otherDistance) {
        return splitSection(appendedStation, otherDistance, this.distance.subtract(otherDistance));
    }

    private List<Section> splitSectionByDownStation(Station appendedStation, Distance otherDistance) {
        return splitSection(appendedStation, this.distance.subtract(otherDistance), otherDistance);
    }

    private List<Section> splitSection(Station appendedStation, Distance upDistance, Distance downDistance) {
        return Lists.newArrayList(
                new Section(line, this.upStation, appendedStation, upDistance),
                new Section(line, appendedStation, this.downStation, downDistance)
        );
    }

    public boolean hasStation(Station station) {
        return isUpStation(station) || isDownStation(station);
    }

    public Line getLine() {
        return line;
    }

    public Distance addDistance(Section downSection) {
        return this.distance.add(downSection.distance);
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
        return line.equals(section.line) && upStation.equals(section.upStation) && downStation.equals(section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation);
    }

    @Override
    public String toString() {
        return "LineStation{" +
                "line=" + line.getName() +
                ", upStation=" + upStation.getName() +
                ", downStation=" + downStation.getName() +
                ", distance=" + distance +
                '}';
    }
}
