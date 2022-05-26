package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    private Integer distance;

    protected Station() {
    }

    Station(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public Integer getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        if (Objects.nonNull(this.line)) {
            this.line.getStations().removeStation(this);
        }
        this.line = line;
        if (!line.getStations().containsStation(this)) {
            line.getStations().addStation(this);
        }
    }

    public void clearLine() {
        this.line = null;
    }

    public void clearRelatedStation() {
        if (line != null) {
            this.line.getStations().removeStation(this);
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

        Station station = (Station) o;

        return id.equals(station.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", line=" + line +
                ", distance=" + distance +
                '}';
    }
}
