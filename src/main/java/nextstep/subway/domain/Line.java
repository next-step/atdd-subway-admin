package nextstep.subway.domain;


import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.generic.domain.distance.Distance;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(name = "distance")
    private Distance distance;

    protected Line() {
    }

    public Line(final String name, final String color, final long distance) {
        this.name = name;
        this.color = color;
        this.distance = Distance.valueOf(distance);
    }

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
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

    public void update(final Line line) {
        this.name = line.name;
        this.color = line.color;
    }

    public List<Station> stations() {
        return Arrays.asList(upStation, downStation);
    }

    public void bindStations(final Station upStation, final Station downStation) {
        this.upStation = upStation;
        this.downStation = downStation;
    }
}
