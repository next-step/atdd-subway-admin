package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        checkDuplicateStation(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void checkDuplicateStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역은 다른 역이어야 합니다.");
        }
    }

    public void connectDownStationTo(Section section) {
        reduceDistance(section.getDistance());
        this.downStation = section.upStation;
    }

    public void connectUpStationTo(Section section) {
        reduceDistance(section.getDistance());
        this.upStation = section.downStation;
    }

    public void disconnectDownStationFrom(Section section) {
        increaseDistance(section.getDistance());
        this.downStation = section.downStation;
    }

    private void increaseDistance(int newDistance) {
        this.distance += newDistance;
    }

    private void reduceDistance(int newDistance) {
        if (this.distance <= newDistance) {
            throw new IllegalArgumentException("기존 역 사이의 길이와 같거나 긴 구간을 등록할 수 없습니다.");
        }

        this.distance -= newDistance;
    }

    public void addLine(Line line) {
        this.line = line;
        /* 연관관계의 주인인 Section에서 Line에 Section을 연결시켜준다 */
        line.getSections().add(this);
    }

    public boolean isDownStation(Station station) {
        return station.equals(downStation);
    }

    public boolean isUpStation(Station station) {
        return station.equals(upStation);
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
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
        return distance == section.distance
                && Objects.equals(id, section.id)
                && Objects.equals(line, section.line)
                && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
