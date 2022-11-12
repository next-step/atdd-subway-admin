package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station lastUpStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station lastDownStation;

    protected Line() {
    }

    public Line(String name, String color, Station lastUpStation, Station lastDownStation) {
        this.name = name;
        this.color = color;
        this.lastUpStation = lastUpStation;
        this.lastDownStation = lastDownStation;
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

    public Station getLastUpStation() {
        return lastUpStation;
    }

    public Station getLastDownStation() {
        return lastDownStation;
    }
}
