package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "UPSTATION_ID")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "DOWNSTATION_ID")
    private Station downStation;

    @Column(nullable = false)
    private Long distance;

    public Line() {
    }

    public Line(final String name, final String color, final Station upStation, final Station downStation, final Long distance) {
        this(null, name, color, upStation, downStation, distance);
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance) {
        this.id = id;
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

    public String getColor() {
        return color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }

    public void setName(String name) {
        if (!name.isEmpty() && !Objects.equals(this.name, name)) {
            this.name = name;
        }
    }

    public void setColor(String color) {
        if (!color.isEmpty() && !Objects.equals(this.color, color)) {
            this.color = color;
        }
    }

    public void setUpStation(Station upStation) {
        if (Objects.nonNull(upStation) && !Objects.equals(this.getUpStation(), upStation)) {
            this.upStation = upStation;
        }
    }

    public void setDownStation(Station downStation) {
        if (Objects.nonNull(downStation) && !Objects.equals(this.getDownStation(), downStation)) {
            this.downStation = downStation;
        }
    }

    public void setDistance(Long distance) {
        if (Objects.nonNull(distance) && distance > 0) {
            this.distance = distance;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(upStation, line.upStation) && Objects.equals(downStation, line.downStation) && Objects.equals(distance, line.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, upStation, downStation, distance);
    }
}
