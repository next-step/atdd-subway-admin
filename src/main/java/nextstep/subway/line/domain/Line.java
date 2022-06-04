package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_line_to_up_station"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_line_to_down_station"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private int distance;

    private String color;

    protected Line() {
    }

    public Line(String name) {
        this.name = name;
    }

    public Line(String name, Station upStation, Station downStation) {
        this.name = name;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Line(String name, Station upStation, Station downStation, int distance, String color) {
        this.name = name;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public void updateName(String name) {
        this.name = name;
    }
}
