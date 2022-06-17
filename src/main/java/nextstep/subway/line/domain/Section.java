package nextstep.subway.line.domain;

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
import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Embedded
    private LineDistance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, LineDistance distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Long id, Station upStation, Station downStation, LineDistance distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section from(Section section) {
        if (Objects.isNull(section)) {
            return null;
        }
        return new Section(section.id, section.upStation, section.downStation, section.distance);
    }

    public static Section of(Station upStation, Station downStation, Integer distance) {
        return new Section(upStation, downStation, new LineDistance(distance));
    }

    public boolean hasSameUpOrDownStation(Section section) {
        return hasSameUpStation(section) || hasSameDownStation(section);
    }

    private boolean hasSameUpStation(Section section) {
        return upStation.equals(section.upStation);
    }

    private boolean hasSameDownStation(Section section) {
        return downStation.equals(section.downStation);
    }

    public void update(Section section) {
        if (hasSameUpStation(section)) {
            updateUpStation(section);
        }
        if (hasSameDownStation(section)) {
            updateDownStation(section);
        }
    }

    private void updateUpStation(Section section) {
        upStation = section.downStation;
        updateDistance(section);
    }

    private void updateDownStation(Section section) {
        downStation = section.upStation;
        updateDistance(section);
    }

    private void updateDistance(Section section) {
        distance = distance.minus(section.distance);
    }

    public boolean isLongerThan(Section section) {
        return distance.isGreaterThan(section.distance);
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public LineDistance getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Section{" + "id=" + id + ", upStation=" + upStation + ", downStation=" + downStation + ", distance="
                + distance + ", line=" + line + '}';
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
        return upStation.equals(section.upStation) && downStation.equals(section.downStation) && distance.equals(
                section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, distance);
    }
}

