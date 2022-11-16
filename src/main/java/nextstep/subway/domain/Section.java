package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station_to_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station_to_station"))
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @Column(nullable = false)
    private int distance;

    public Section() {
    }

    public Section (Station upStation, Station downStation, Line line, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
    }


    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance;
    }


    public boolean isContainSameStations(Section section) {
        if (Objects.isNull(section)) {
            return false;
        }
        return isUpStation(section.upStation) && isDownStation(section.downStation);
    }

    public boolean isContainAnyStaion(Section section) {
        if (Objects.isNull(section)) {
            return false;
        }
        return isUpStation(section.upStation) || isDownStation(section.downStation)
                || isUpStation(section.getDownStation()) || isDownStation(section.getDownStation());
    }

    public boolean isUpStation(Station station) {
        return this.upStation.equals(station);
    }
    public boolean isDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public void change(Section newSection) {
        if (Objects.isNull(newSection)) {
            return;
        }
        changeStation(newSection);
    }

    private void changeDistance(Section newSection) {
        this.distance -= newSection.getDistance();
    }

    private void changeStation(Section newSection) {

        if (isUpStation(newSection.upStation)) {
            changeUpStation(newSection);
            changeDistance(newSection);
            return;
        }

        if (isDownStation(newSection.downStation)) {
            changeDownStation(newSection);
            changeDistance(newSection);
        }
    }

    public void updateLine(Line line) {
        this.line = line;
    }

    private void changeUpStation(Section newSection) {
        this.upStation = newSection.downStation;
    }
    private void changeDownStation(Section newSection) {
        this.downStation = newSection.upStation;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, line, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", line=" + line +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(line, section.line);
    }

}
