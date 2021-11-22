package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "section", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"line_id", "station_id"})
})
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    protected Section() {

    }

    private Section(Line line, Station station) {
        this.line = line;
        this.station = station;
    }

    public static Section create(Line line, Station station) {
        return new Section(line, station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(line, section.line) && Objects.equals(station, section.station);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, station);
    }
}
