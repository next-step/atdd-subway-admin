package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private Name name;
    @Embedded
    private Color color;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_line_to_up_station"))
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_line_to_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Line() {
    }

    public Line(Name name, Color color, Station upStation, Station downStation, Distance distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Color getColor() {
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

    public void update(String name, String color) {
        this.name = new Name(name);
        this.color = new Color(color);
    }
}
