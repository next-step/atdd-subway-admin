package nextstep.subway.section.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {

    private static final String NEW_DISTANCE_BIGGER_ERROR = "새로운 구간의 길이가 기존의 길이보다 길수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.from(distance);
    }

    private Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public static Section of(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public List<Station> getBothStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public boolean isSameBothStation(Section other) {
        return this.upStation.equals(other.upStation)
                && this.downStation.equals(other.downStation);
    }

    public void modifySectionFor(Section newSection) {
        if (this.upStation.equals(newSection.upStation)) {
            this.upStation = newSection.downStation;
            changeDistance(newSection);
        }
        if (this.downStation.equals(newSection.downStation)) {
            this.downStation = newSection.upStation;
            changeDistance(newSection);
        }
    }

    private void changeDistance(Section newSection) {
        if (newSection.distance.isBiggerAndEqual(this.distance)) {
            throw new IllegalArgumentException(NEW_DISTANCE_BIGGER_ERROR);
        }
        Distance newDistance = this.distance.minus(newSection.distance);
        this.distance.update(newDistance);
    }

    public Section merge(Section nextSection) {
        return Section.of(this.upStation, nextSection.getDownStation(), distance.merge(nextSection.distance));
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
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(
                upStation, section.upStation) && Objects.equals(downStation, section.downStation)
                && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, line);
    }

}
