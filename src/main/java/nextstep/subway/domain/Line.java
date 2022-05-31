package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Embedded
    @Column(nullable = false)
    private Distance distance;

    protected Line() {
    }

    public Line(final String name, final String color, final Distance distance) {
        this(name, color, null, null, distance);
    }

    public Line(final String name, final String color, final Station upStation, final Station downStation, final Distance distance) {
        this(null, name, color, upStation, downStation, distance);
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
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

    public Distance getDistance() {
        return distance;
    }

    public void updateName(String name) {
        if (!name.isEmpty() && !Objects.equals(this.name, name)) {
            this.name = name;
        }
    }

    public void updateColor(String color) {
        if (Objects.nonNull(color) && !color.isEmpty() && !Objects.equals(this.color, color)) {
            this.color = color;
        }
    }

    public Line upStationBy(final Station upStation) {
        this.upStation = upStation;
        return this;
    }

    public void updateBy(LineRequest request) {
        this.updateName(request.getName());
        this.updateColor(request.getColor());
    }

    public Line downStationBy(Station downStation) {
        this.downStation = downStation;
        return this;
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
