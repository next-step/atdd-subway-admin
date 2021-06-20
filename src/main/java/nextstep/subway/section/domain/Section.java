package nextstep.subway.section.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.setUpStation(upStation);
        this.setDownStation(downStation);
        this.distance = distance;
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.setLine(line);
        this.setUpStation(upStation);
        this.setDownStation(downStation);
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id) &&
            Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", line=" + line +
            '}';
    }
}