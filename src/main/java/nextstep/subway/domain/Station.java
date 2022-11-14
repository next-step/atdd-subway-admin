package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "station")
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_station_to_line"))
    private Line line;

    protected Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public void toLine(Line line) {
        this.line = line;
        List<Station> stations = line.getStations();
        if (!stations.contains(this)) {
            stations.add(this);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station station = (Station) o;
        return Objects.equals(getName(), station.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
