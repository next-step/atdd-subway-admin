package nextstep.subway.line.domain;

import nextstep.subway.common.entity.BaseEntity;
import nextstep.subway.line.dto.Distance;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

/**
 * packageName : nextstep.subway.section
 * fileName : Section
 * author : haedoang
 * date : 2021/11/20
 * description : 구간 엔티티
 */
@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int distance;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "STATION_ID")
    private Station station;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "NEXT_STATION_ID")
    private Station nextStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "LINE_ID")
    private Line line;

    protected Section() {
    }

    public Section(Station station, Station nextStation, Distance distance) {
        this.station = station;
        this.nextStation = nextStation;
        this.distance = distance.intValue();
    }

    public boolean isSameStation(Section section) {
        return station.equals(section.station);
    }

    public boolean isSameNextStation(Section section) {
        return nextStation.equals(section.nextStation);
    }

    public void updateStation(Section section) {
        this.station = section.nextStation;
        this.distance -= section.distance;
    }

    public void updateNextStation(Section section) {
        this.nextStation = section.station;
        this.distance -= section.distance;
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public boolean isPermitDistance(int distance) {
        return this.distance > distance;
    }

    public boolean isDuplicate(Section section) {
        return station.equals(section.getStation()) && nextStation.equals(section.getNextStation());
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public Station getStation() {
        return station;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(getId(), section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
