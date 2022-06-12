package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "station")
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        if (this.line != null) {
            this.line.getStations().remove(this);
        }
        this.line = line;
        if (this.line != null && !this.line.getStations().contains(this)) {
            this.line.addStation(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name) && Objects.equals(line, station.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, line);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", line=" + line +
                '}';
    }
}
