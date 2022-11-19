package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SECTION")
public class Section extends BaseEntity {

    private static int MIN_DISTANCE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    private Integer distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {}

    private Section(Long id, Station upStation, Station downStation, Integer distance, Line line) {
        validateStations(upStation, downStation);
        validateDistance(distance);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public static Section of(Station upStation, Station downStation, Integer distance) {
        return new Section(null, upStation, downStation, distance, null);
    }

    public static Section of(Station upStation, Station downStation, Integer distance, Line line) {
        return new Section(null, upStation, downStation, distance, line);
    }

    private void validateStations(Station... stations) {
        if (Objects.isNull(stations) || Arrays.stream(stations).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("구간은 상행역, 하행역 둘다 존재해야 합니다.");
        }
    }

    private void validateDistance(Integer distance) {
        if (Objects.isNull(distance) || distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(String.format("구간길이는 %d 이상이여 합니다.", MIN_DISTANCE));
        }
    }

    public void update(Section section) {
        validateContainsAllStation(section);
        if (this.upStation.equals(section.upStation)) {
            validateUpdateDistance(section.distance);
            updateWhenEqualsUpStation(section);
        }
        if (this.downStation.equals(section.downStation)) {
            validateUpdateDistance(section.distance);
            updateWhenEqualsDownStation(section);
        }
    }

    private void updateWhenEqualsUpStation(Section section) {
        this.upStation = section.downStation;
        this.distance = this.distance - section.distance;
    }

    private void updateWhenEqualsDownStation(Section section) {
        this.downStation = section.upStation;
        this.distance = this.distance - section.distance;
    }

    private void validateContainsAllStation(Section section) {
        if (getStations().containsAll(section.getStations())) {
            throw new IllegalArgumentException("구간의 두 역은 일치할 수 없습니다.");
        }
    }

    private void validateUpdateDistance(Integer distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우는 길이가 기존 구간길이보다 작아야 합니다.");
        }
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
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
