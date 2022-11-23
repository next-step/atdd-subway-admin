package nextstep.subway.domain.line;

import nextstep.subway.domain.station.Station;

import javax.persistence.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (!Objects.equals(id, line.id)) return false;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
