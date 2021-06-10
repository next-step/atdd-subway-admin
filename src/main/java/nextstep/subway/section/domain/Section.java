package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    public Section() { }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = validDistance(distance);
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    private int validDistance(int distance) {
        if (distance < 1 || distance >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("유효하지 않은 Section 의 역들 사이거리 입니다.");
        }
        return distance;
    }

    public boolean isInFrontOf(Section section) {
        return this.downStation.compareName(section.upStation());
    }

    public boolean isInMidFrontOf(Section section) {
        return this.upStation.compareName(section.upStation());
    }

    public boolean isInMidRearOf(Section section) {
        return this.downStation.compareName(section.downStation());
    }

    public boolean isBehindOf(Section section) {
        return this.upStation.compareName(section.downStation());
    }

    public void connectBehindOf(Section section) {
        this.upStation = section.downStation();
    }

    public void connectInFrontOf(Section section) {
        this.downStation = section.upStation();
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    protected Station upStation() {
        return upStation;
    }

    protected Station downStation() {
        return downStation;
    }

    public List<Station> upDownStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Long upStationId() {
        return upStation.getId();
    }

    public Long downStationId() {
        return downStation.getId();
    }

    public Long lineId() {
        return line.getId();
    }

    public String upStationName() {
        return upStation.getName();
    }

    public String downStationName() {
        return downStation.getName();
    }

    public boolean bothStationsAreAlreadyIn(List<Station> stations) {
        return stations.contains(this.upStation)
                && stations.contains(this.downStation);
    }

    public boolean bothStationsAreNotIn(List<Station> stations) {
        return !stations.contains(this.upStation)
                && !stations.contains(this.downStation);
    }

    public int distance() {
        return distance;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

}
