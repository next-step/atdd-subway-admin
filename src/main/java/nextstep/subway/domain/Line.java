package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @Column(nullable = false)
    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    protected Line() {
    }

    private Line(Long id, String name, String color, int distance, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static Line of(String name, String color, int distance, Station upStation, Station downStation) {
        return new Line(null, name, color,distance, upStation,downStation );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor(){
        return color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void updateNameColor(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
