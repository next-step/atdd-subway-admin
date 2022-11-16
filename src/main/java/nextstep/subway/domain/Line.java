package nextstep.subway.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String color;
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    private Integer distance;

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, Integer distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(getName(), line.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
