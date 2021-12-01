package nextstep.subway.line.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    @ManyToOne
    private Line line;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Line line, Station upStation, Station downStation, int distance) {
        validateNull(upStation, downStation);
        validateDuplicate(upStation, downStation);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.from(distance);
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
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

    public Line getLine() {
        return line;
    }

    public boolean isDistanceGreaterThan(Section addSection) {
        return this.distance.isGreaterThan(addSection.distance);
    }

    public boolean isEqualsUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isEqualsDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public void changeDownStationToAddSectionUpStation(Section addSection) {
        this.upStation = addSection.getDownStation();
        this.distance.minus(addSection.distance);
    }

    private void validateNull(Station upStation, Station downStation) {
        if (upStation == null || downStation == null) {
            throw new BadRequestException("종점역 정보가 입력되지 않았습니다.");
        }
    }

    private static void validateDuplicate(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new BadRequestException("상행선과 하행선은 같을 수 없습니다.");
        }
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", line=" + line +
                ", distance=" + distance +
                '}';
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
