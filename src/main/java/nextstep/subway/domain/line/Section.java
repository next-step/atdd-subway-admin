package nextstep.subway.domain.line;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.domain.station.Station;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "line_id",
        foreignKey = @ForeignKey(name = "fk_section_to_line")
    )
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "up_station_id",
        foreignKey = @ForeignKey(name = "fk_section_to_up_station")
    )
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "down_station_id",
        foreignKey = @ForeignKey(name = "fk_section_to_down_station")
    )
    private Station downStation;

    private Integer distance;

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, Integer distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
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
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Section{" +
            "line=" + line.getName() +
            ", upStation=" + upStation.getName() +
            ", downStation=" + downStation.getName() +
            '}';
    }
}
