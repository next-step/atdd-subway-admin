package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Embedded
    private Distance distance = new Distance();

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
        this.distance = new Distance(distance);
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
        return distance.value();
    }

    public void repair(Section section) {
        if (Objects.isNull(section)) {
            return;
        }
        repairStation(section);
    }

    private void repairStation(Section section) {
        if (isSameUpStation(section.upStation)) {
            repairUpStation(section);
            return;
        }

        if (isSameDownStation(section.downStation)) {
            repairDownStation(section);
        }
    }

    private void repairUpStation(Section section) {
        this.upStation = section.downStation;
        changeDistance(section.distance);
    }

    private void repairDownStation(Section section) {
        this.downStation = section.upStation;
        changeDistance(section.distance);
    }

    private void changeDistance(Distance distance) {
        this.distance.minus(distance);
    }

    public boolean isSame(Section section) {
        return isSameUpStation(section.upStation) && isSameDownStation(section.downStation);
    }

    private boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isSameDownStation(Station station) {
        return this.downStation.equals(station);
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
