package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.dto.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this.setUpStation(upStation);
        this.setDownStation(downStation);
        this.distance = distance;
    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.setLine(line);
        this.setUpStation(upStation);
        this.setDownStation(downStation);
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    /**
     * 기존의 거리와 주어진 거리의 차이를 distance로 세팅합니다.
     * @param distance
     */
    public void differenceDistance(Distance distance) {
        this.distance = this.distance.difference(distance);
    }

    /**
     * 기존의 거리와 주어진 거리의 합을 distance로 세팅합니다.
     * @param distance
     */
    public void addDistance(Distance distance) {
        this.distance = this.distance.add(distance);
    }

    public Long getUpStationId() {
        return this.getUpStation().getId();
    }

    public Long getDownStationId() {
        return this.getDownStation().getId();
    }

    public boolean isEqualsUpStationId(Long stationId) {
        return this.getUpStation().getId().equals(stationId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) &&
                Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line +
                '}';
    }
}
