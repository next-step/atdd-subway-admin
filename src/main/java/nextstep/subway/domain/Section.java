package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @Column(nullable = false)
    private Long distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, Long distance) {
        return new Section(line,upStation,downStation,distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(line, section.line) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, line, distance);
    }

    public boolean hasUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }
    public boolean hasDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    public boolean hasBiggerDistance(long distance) {
        return this.distance > distance;
    }

    public void splitFromUpStation(Station newStation, long distance) {
        Section newSection = Section.of(line, newStation, this.downStation, this.distance - distance);
        line.addSection(newSection);
        this.downStation = newStation;
        this.distance = distance;
    }


    public void splitFromDownStation(Station newStation, long distance) {
        Section newSection = Section.of(line, this.upStation, newStation, this.distance - distance);
        line.addSection(newSection);
        this.upStation = newStation;
        this.distance = distance;
    }
}
