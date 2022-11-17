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

@Entity
public class Section {
    private static final String DISTANCE_OVER_ERROR_MESSAGE = "추가할 구간의 거리가 기존 역 사이 거리보다 길 수는 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private Long distance;

    protected Section() {
    }

    public Section(Long id, Line line, Station upStation, Station downStation, Long distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long id, Station upStation, Station downStation, Long distance) {
        this(id, null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, Long distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(Station upStation, Station downStation, Long distance) {
        this(null, null, upStation, downStation, distance);
    }

    public void modify(Section section) {
        modifyUpStation(section);
        modifyDownStation(section);
    }

    private void modifyUpStation(Section section) {
        if (!Objects.equals(this.upStation, section.upStation)) {
            return;
        }
        this.upStation = section.downStation;
        modifyDistance(section.distance);
    }

    private void modifyDownStation(Section section) {
        if (!Objects.equals(this.downStation, section.downStation)) {
            return;
        }
        this.downStation = section.upStation;
        modifyDistance(section.distance);
    }

    private void modifyDistance(Long distance) {
        validateDistanceOver(distance);
        this.distance = this.distance - distance;
    }

    private void validateDistanceOver(Long distance) {
        if (distance >= this.distance) {
            throw new IllegalArgumentException(DISTANCE_OVER_ERROR_MESSAGE);
        }
    }

    public List<Station> getStations() {
        return Arrays.asList(this.upStation, this.downStation);
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return Objects.equals(getId(), section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
