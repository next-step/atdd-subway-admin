package nextstep.subway.section.domain;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
@Table(
    name = "section",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_section",
            columnNames = {"line_id", "up_station_id", "down_station_id"}
        )
    }
)
public class Section extends BaseEntity {

    public static final String UP_AND_DOWN_STATIONS_CANNOT_BE_THE_SAME = "구간의 상행역과 하행역은 같을 수 없습니다.";
    public static final int MIN_DISTANCE = 0;
    public static final String DISTANCE_MUST_BE_AT_LEAST_MIN_DISTANCE = "거리는 %d 이상이어야 합니다.";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_upstation"))
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_downstation"))
    private Station downStation;

    private int distance;

    public Section(Long upStationId, Long downStationId, int distance) {
        this(null, upStationId, downStationId, distance);
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        validationSection(upStationId, downStationId);
        validationDistance(distance);
        this.line = new Line(lineId);
        this.upStation = new Station(upStationId);
        this.downStation = new Station(downStationId);
        this.distance = distance;
    }

    public void validationDistance(int distance) {
        if (distance == MIN_DISTANCE) {
            throw new IllegalArgumentException(String.format(DISTANCE_MUST_BE_AT_LEAST_MIN_DISTANCE, MIN_DISTANCE));
        }
    }

    public void validationSection(Long upStationId, Long downStationId) {
        if (upStationId == downStationId) {
            throw new IllegalArgumentException(UP_AND_DOWN_STATIONS_CANNOT_BE_THE_SAME);
        }
    }

    protected Section() {

    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public void toLine(Line line) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects
            .equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation);
    }

    public boolean isBefore(Section section) {
        return upStation.equals(section.getDownStation());
    }

    public boolean isAfter(Section section) {
        return downStation.equals(section.getUpStation());
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", line.id=" + line.getId() +
            ", upStation.id=" + upStation.getId() +
            ", downStation.id=" + downStation.getId() +
            ", distance=" + distance +
            '}';
    }
}
