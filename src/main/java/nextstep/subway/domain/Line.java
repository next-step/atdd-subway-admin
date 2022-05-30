package nextstep.subway.domain;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Where(clause = "deleted=false")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private Boolean deleted = false;

    protected Line() {

    }

    public Line(String name, String color, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static Line of(String name, String color, Station upStation, Station downStation) {
        return new Line(name, color, upStation, downStation);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDelete() {
        return this.deleted;
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
}
