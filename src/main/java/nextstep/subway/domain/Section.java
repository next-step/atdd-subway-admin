package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    protected Section() {
    }

    public Section(int distance, Station upStation, Station downStation) {
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void repair(Section section) {
        if (Objects.isNull(section)) {
            return;
        }
        repairUpStation(section);
        repairDownStation(section);
    }

    private void repairUpStation(Section section) {
        if (isSameUpStation(section.upStation)) {
            this.upStation = section.downStation;
            minusDistance(section.distance);
        }
    }

    private void repairDownStation(Section section) {
        if (isSameDownStation(section.downStation)) {
            this.downStation = section.upStation;
            minusDistance(section.distance);
        }
    }

    private boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isSameDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public void minusDistance(int distance) {
        this.distance -= distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
