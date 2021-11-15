package nextstep.subway.line.domain;

import nextstep.subway.global.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private Distance distance;

    @ManyToOne
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        validation(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void changeLine(Line line) {
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

    public boolean isLessThanOrEquals(Section section) {
        return this.distance.isLessThanOrEquals(section.distance);
    }

    public boolean isUpStationEquals(Section section) {
        return this.upStation.equals(section.upStation);
    }

    public boolean isUpStationEquals(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isDownStationEquals(Section section) {
        return this.downStation.equals(section.downStation);
    }

    public boolean isDownStationEquals(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isUpStationEqualsWithDownStation(Section section) {
        return this.upStation.equals(section.getDownStation());
    }

    public boolean isDownStationEqualsWithUpStation(Section section) {
        return this.downStation.equals(section.getUpStation());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance) && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, line);
    }

    private void validation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역은 같을 수 없습니다.");
        }
    }

    public Distance getRemainDistance(Section section) {
        return new Distance(this.distance.getRemainDistance(section.distance));
    }
}
