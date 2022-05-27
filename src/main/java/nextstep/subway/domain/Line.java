package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.dto.LineRequest;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    private Long distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    protected Line() {

    }

    public Line(String name, String color, Long distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static Line of(LineRequest lineRequest, Station upStation, Station downStation) {
        return new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getDistance(), upStation, downStation);
    }

    public void update(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
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

    public Long getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
