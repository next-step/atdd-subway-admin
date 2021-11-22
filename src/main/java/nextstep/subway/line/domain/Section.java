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

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    private Double position;

    @Transient
    public static final double UP_SECTION = 65535.0;

    @Transient
    public static final double DOWN_SECTION = UP_SECTION * 2;

    protected Section() {

    }

    private Section(Line line, Station station, Double position) {
        this.line = line;
        this.station = station;
        this.position = position;
    }

    public static Section create(Line line, Station station) {
        return new Section(line, station, 65535.0);
    }

    public static Section create(Line line, Station station, Double position) {
        return new Section(line, station, position);
    }

    public void updatePosition(Double position) {
        this.position = position;
    }

    public boolean matchPosition(Section section) {
        return matchPosition(section.getPosition());
    }

    public boolean matchPosition(Double position) {
        return this.position == position;
    }

    public Double getPosition() {
        return position;
    }

    public Station getStation() {
        return station;
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
