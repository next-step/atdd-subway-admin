package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.station.domain.Station;
import org.springframework.util.Assert;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, Distance distance) {
        Assert.notNull(upStation, "'upStation' must not be null");
        Assert.notNull(downStation, "'downStation' must not be null");
        Assert.notNull(distance, "'distance' must not be null");
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
    }

    List<Station> stations() {
        return Arrays.asList(upStation, downStation);
    }

    Station upStation() {
        return upStation;
    }

    Station downStation() {
        return downStation;
    }

    void setLine(Line line) {
        this.line = line;
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
        return Objects.equals(id, section.id) && Objects
            .equals(upStation, section.upStation) && Objects
            .equals(downStation, section.downStation) && Objects
            .equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", upStation=" + upStation +
            ", downStation=" + downStation +
            ", distance=" + distance +
            '}';
    }

    public void changeUpStation(Section section) {
        this.upStation = section.downStation;
        subtractDistance(section.distance);
    }

    public void changeDownStation(Section section) {
        this.downStation = section.upStation;
        subtractDistance(section.distance);
    }

    public boolean equalsUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean equalsDownStation(Station station) {
        return downStation.equals(station);
    }

    private void subtractDistance(Distance distance) {
        this.distance = this.distance.subtract(distance);
    }
}
