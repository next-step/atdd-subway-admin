package nextstep.subway.domain.line;

import nextstep.subway.domain.station.Station;

import javax.persistence.*;

@Entity
@Table(name = "line")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineName name;

    @Embedded
    private LineColor color;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    protected Line() {

    }

    public Line(LineName name, LineColor color, Distance distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public Long getId() {
        return this.id;
    }

    public LineName getName() {
        return this.name;
    }

    public void changeName(LineName name) {
        this.name = name;
    }

    public LineColor getColor() {
        return this.color;
    }

    public void changeColor(LineColor color) {
        this.color = color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public void toUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void toDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void delete() {
        this.upStation = null;
        this.downStation = null;
    }
}
