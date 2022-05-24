package nextstep.subway.section.domain;

import static nextstep.subway.constants.SectionExceptionMessage.*;

import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Distance distance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    protected Section() {}

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Distance distance) {
        validateSection(upStation, downStation, distance);
        return new Section(upStation, downStation, distance);
    }

    private static void validateSection(Station upStation, Station downStation, Distance distance) {
        if (Objects.isNull(upStation)) {
            throw new IllegalArgumentException(UP_STATION_IS_NOT_NULL);
        }

        if (Objects.isNull(downStation)) {
            throw new IllegalArgumentException(DOWN_STATION_IS_NOT_NULL);
        }

        if (Objects.isNull(distance)) {
            throw new IllegalArgumentException(DISTANCE_IS_NOT_NULL);
        }

        if(upStation.equals(downStation)) {
            throw new IllegalArgumentException(CANNOT_EQUALS_UP_STATION_WITH_DOWN_STATION);
        }
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public Distance getDistance() {
        return this.distance;
    }

    public void registerLine(Line line) {
        this.line = line;
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
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
