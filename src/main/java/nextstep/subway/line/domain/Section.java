package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.CannotAddException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity implements Comparable<Section>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Distance distance;

    @ManyToOne
    @JoinColumn(name = "from_station_id", nullable = false)
    private Station fromStation;

    @ManyToOne
    @JoinColumn(name = "to_station_id", nullable = false)
    private Station toStation;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    private Section(Distance distance, Station fromStation, Station toStation) {
        this.distance = distance;
        this.fromStation = fromStation;
        this.toStation = toStation;
    }

    public static Section create(Distance distance, Station fromStation, Station toStation, Line line) {
        return new Section(distance, fromStation, toStation).toLine(line);
    }

    public List<Station> getFromToStations() {
        return Arrays.asList(fromStation, toStation);
    }

    /**
     * 연관관계 편의 메서드
     *
     * @param line
     */
    public Section toLine(Line line) {
        if (this.line != null) {
            this.line.removeSection(this);
        }

        this.line = line;
        if (!line.containsLineStation(this)) {
            line.addSection(this);
        }
        return this;
    }

    public void removeLine() {
        this.line = null;
    }

    boolean equalsLine(Line line) {
        if (this.line == null) {
            return false;
        }
        return this.line.equals(line);
    }

    boolean isFromStation(Station station) {
        return fromStation.equals(station);
    }

    boolean isToStation(Station station) {
        return toStation.equals(station);
    }

    Section updateFromStation(Distance distance, Station fromStation) {
        return update(minusDistance(distance), fromStation, this.toStation);
    }

    Section updateToStation(Distance distance, Station toStation) {
        return update(minusDistance(distance), this.fromStation, toStation);
    }

    Section updateByRemoveSection(Section section) {
        return update(plusDistance(section.distance), this.fromStation, section.toStation);
    }

    private Section update(Distance distance, Station fromStation, Station toStation) {
        this.distance = distance;
        this.fromStation = fromStation;
        this.toStation = toStation;
        return this;
    }

    private Distance plusDistance(Distance distance) {
        return this.distance.plus(distance);
    }

    private Distance minusDistance(Distance distance) {
        try {
            return this.distance.minus(distance);
        }catch (BusinessException e) {
            throw new CannotAddException(Messages.LONG_OR_SAME_DISTANCE.getValues());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section other = (Section) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Section o) {

        if (this.fromStation.equals(o.toStation)) {
            return 1;
        }

        if (this.toStation.equals(o.fromStation)) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", distance=" + distance +
            ", fromStation=" + fromStation.getName() +
            ", toStation=" + toStation.getName() +
            ", line=" + line.getName() +
            '}';
    }
}
