package nextstep.subway.domain.section;

import nextstep.subway.domain.BaseEntity;
import nextstep.subway.domain.line.Distance;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;
import nextstep.subway.message.SectionMessage;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "section")
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        validateStations(upStation, downStation);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateStations(Station upStation, Station downStation) {
        Objects.requireNonNull(upStation, SectionMessage.ERROR_UP_STATION_SHOULD_BE_NOT_NULL.message());
        Objects.requireNonNull(downStation, SectionMessage.ERROR_DOWN_STATION_SHOULD_BE_NOT_NULL.message());
        if(upStation.equals(downStation)) {
            throw new IllegalArgumentException(SectionMessage.ERROR_UP_AND_DOWN_STATION_SHOULD_BE_NOT_EQUAL.message());
        }
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public boolean isSameDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void minusDistance(Section section) {
        this.distance = this.distance.minus(section.distance);
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }

    public void changeDistance(Section section) {
        this.distance = section.distance;
    }

    public Distance getDistance() {
        return this.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        if (!Objects.equals(id, section.id)) return false;
        if (!Objects.equals(line, section.line)) return false;
        if (!Objects.equals(upStation, section.upStation)) return false;
        return Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (line != null ? line.hashCode() : 0);
        result = 31 * result + (upStation != null ? upStation.hashCode() : 0);
        result = 31 * result + (downStation != null ? downStation.hashCode() : 0);
        return result;
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
