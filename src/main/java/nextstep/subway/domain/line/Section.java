package nextstep.subway.domain.line;

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
import nextstep.subway.domain.line.wrap.Distance;
import nextstep.subway.domain.station.Station;

@Entity
public class Section {

    public static final String SECTION_DISTANCE_OVER_DISTANCE_ERROR_MESSAGE = "추가하는 구간의 길이는 연결되는 구간의 길이보다 크거나 같을 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "line_id",
        foreignKey = @ForeignKey(name = "fk_section_to_line")
    )
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "up_station_id",
        foreignKey = @ForeignKey(name = "fk_section_to_up_station")
    )
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "down_station_id",
        foreignKey = @ForeignKey(name = "fk_section_to_down_station")
    )
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
    }

    public void swapUpStationToTargetDownStation(Section targetSection) {
        validateDistance(targetSection.getDistance());
        this.upStation = targetSection.getDownStation();
        this.distance.updateDistance(targetSection.getDistance());
    }

    public void swapDownStationToTargetUpStation(Section targetSection) {
        validateDistance(targetSection.getDistance());
        this.downStation = targetSection.getUpStation();
        this.distance.updateDistance(targetSection.getDistance());
    }

    private void validateDistance(Integer targetDistance) {
        if (distance.isOverOrEquals(targetDistance)) {
            throw new IllegalArgumentException(SECTION_DISTANCE_OVER_DISTANCE_ERROR_MESSAGE);
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

    public Integer getDistance() {
        return distance.getDistance();
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
