package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Line line, Station downStation) {
        this(line, null, downStation, null);
    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void update(Section section) {
        updateUpStation(section);
        updateDownStation(section);
    }

    private void updateUpStation(Section section) {
        if (isSameUpStation(section.upStation)) {
            this.upStation = section.downStation;
            minus(section);
        }
    }

    private void updateDownStation(Section section) {
        if (isSameDownStation(section.downStation)) {
            this.downStation = section.upStation;
            minus(section);
        }
    }

    private void minus(Section section) {
        try {
            this.distance = Distance.subtract(this.distance, section.distance);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("추가하려는 구간의 거리는 현재 거리보다 작아야 합니다.");
        }
    }

    public static Section mergeTwoSection(Section upStation, Section downStation) {
        Distance distance = Distance.add(upStation.distance, downStation.distance);
        return new Section(upStation.line, downStation.upStation, upStation.downStation, distance);
    }

    public boolean isSameDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isSameUpStation(Station station) {
        return upStation.equals(station);
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public Line line() {
        return line;
    }

    public Distance distance() {
        return distance;
    }

    public List<Station> stations() {
        return Arrays.asList(this.upStation, this.downStation);
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
        return Objects.equals(id, section.id) && Objects.equals(line, section.line)
                && Objects.equals(upStation, section.upStation) && Objects.equals(
                downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
