package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private Long distance;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, Long distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public static Section of(Station upStation, Station downStation, Long distance, Line line) {
        return new Section(upStation, downStation, distance, line);
    }

    public boolean isContains(Station station) {
        return upStation == station || downStation == station;
    }

    public boolean isUpStationOrDownStation() {
        return this.downStation.equals(line.getUpStation())
                || this.upStation.equals(line.getDownStation());
    }

    public void updateSection(Section newSection) {
        this.distance -= newSection.getDistance();

        if (this.upStation.equals(newSection.getUpStation())) {
            this.upStation = newSection.getDownStation();
        }

        if (this.getDownStation().equals(newSection.getDownStation())) {
            this.downStation = newSection.getUpStation();
        }
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

    public Long getDistance() {
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
        return Objects.equals(getId(), section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
