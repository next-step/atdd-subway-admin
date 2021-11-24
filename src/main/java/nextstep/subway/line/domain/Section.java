package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Line line;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Integer distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
        this.line.addSection(this);
    }

    public void shiftBack(Section section) {
        if (this.upStation.isSameName(section.getUpStation())) {
            this.upStation = section.getDownStation();
            this.distance.minus(section.getDistance());
        }
    }

    public void shiftForward(Section section) {
        if (this.downStation.isSameName(section.getDownStation())) {
            this.downStation = section.getUpStation();
            this.distance.minus(section.getDistance());
        }
    }

    public void shift(Section section) {
        shiftBack(section);
        shiftForward(section);
    }

    public boolean isDuplicate(Section section) {
        return (upStation.isSameName(section.getUpStation()) && downStation.isSameName(section.getDownStation()))
                || (upStation.isSameName(section.getDownStation()) && downStation.isSameName(section.getUpStation()));
    }

    public boolean anyMatched(Section section) {
        return upStation.isSameName(section.getUpStation()) || upStation.isSameName(section.getDownStation()) ||
                downStation.isSameName(section.getUpStation()) || downStation.isSameName(section.getDownStation());
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance.value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        if (id == null) return false;
        return id.equals(section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
