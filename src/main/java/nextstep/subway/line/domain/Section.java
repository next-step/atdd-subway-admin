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
    private Distance distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Long id, Station upStation, Station downStation, Distance distance) {
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
        return new Section(upStation, downStation, new Distance(distance));
    }

    public void merge(Section section) {
        isMergeable(section);

        mergeByUpStation(section);
        mergeByDownStation(section);

        distance = distance.add(section.distance);
    }

    private void mergeByUpStation(Section section) {
        if (hasUpStation(section.downStation)) {
            upStation = section.upStation;
        }
    }

    private void mergeByDownStation(Section section) {
        if (hasDownStation(section.upStation)) {
            downStation = section.downStation;
        }
    }

    private void isMergeable(Section section) {
        if (!hasDownStation(section.upStation) && !hasUpStation(section.downStation)) {
            throw new IllegalArgumentException("연결된 역을 찾을 수 없어 두 구간을 합칠 수 없습니다.");
        }
    }

    public boolean hasStation(Station station) {
        return hasUpStation(station) || hasDownStation(station);
    }

    public boolean hasSameUpOrDownStation(Section section) {
        return hasUpStation(section.upStation) || hasDownStation(section.downStation);
    }

    public boolean hasUpStation(Station station) {
        return upStation.equals(station);
    }

    private boolean hasDownStation(Station station) {
        return downStation.equals(station);
    }

    public void update(Section section) {
        validateStations(section);

        if (hasUpStation(section.upStation)) {
            updateUpStation(section);
        }
        if (hasDownStation(section.downStation)) {
            updateDownStation(section);
        }
    }

    private void validateStations(Section section) {
        if (hasUpStation(section.upStation) && hasDownStation(section.downStation)) {
            throw new IllegalArgumentException("동일한 상하행역을 가진 구간을 추가할 수 없습니다.");
        }
    }

    private void updateUpStation(Section section) {
        validateDistance(section);

        upStation = section.downStation;
        updateDistance(section);
    }

    private void updateDownStation(Section section) {
        validateDistance(section);

        downStation = section.upStation;
        updateDistance(section);
    }

    private void validateDistance(Section section) {
        if (!distance.isGreaterThan(section.distance)) {
            throw new IllegalArgumentException(
                    String.format("새로운 구간의 거리(%s)가 현재 구간의 거리(%s)보다 클 수 없습니다.", section.distance, distance));
        }
    }

    private void updateDistance(Section section) {
        distance = distance.minus(section.distance);
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Long getId() {
        return id;
    }

    public Distance getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Section{" + "id=" + id + ", upStation=" + upStation + ", downStation=" + downStation + ", distance="
                + distance + '}';
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

