package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    private int distance;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    private Station upStation;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    private Station downStation;

    protected Line() {
    }

    public Line(Long id, String name, String color, int distance,
        Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Line(String name, String color, int distance, Station upStation,
        Station downStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void merge(final Line otherLine) {
        this.name = otherLine.name;
        this.color = otherLine.color;
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
