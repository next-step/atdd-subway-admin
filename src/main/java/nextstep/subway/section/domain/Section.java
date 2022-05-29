package nextstep.subway.section.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    private static final String UP_DOWN_STATION_NOT_NULL = "상/하행 방향 역은 빈값일 수 없습니다.";
    private static final String UP_DOWN_STATION_NOT_EQUALS = "상/하행 방향 역은 동일할 수 없습니다.";
    private static final String DISTANCE_NOT_NULL = "지하철 구간 길이는 빈값일 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    protected Section() {}

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        validate();
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(upStation, downStation, Distance.from(distance));
    }

    private void validate() {
        if (Objects.isNull(this.upStation) || Objects.isNull(this.downStation)) {
            throw new IllegalArgumentException(UP_DOWN_STATION_NOT_NULL);
        }

        if (this.upStation.equals(this.downStation)) {
            throw new IllegalArgumentException(UP_DOWN_STATION_NOT_EQUALS);
        }

        if (Objects.isNull(this.distance)) {
            throw new IllegalArgumentException(DISTANCE_NOT_NULL);
        }
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public List<Station> getStations() {
        return Arrays.asList(this.upStation, this.downStation);
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
