package nextstep.subway.domain;

import nextstep.subway.exception.InvalidSectionException;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class Section extends BaseEntity {
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

    public Section(Line line, Station upStation, Station downStation, Long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, Long distance) {
        validateIncludeAnyStation(line, upStation, downStation);
        return new Section(line, upStation, downStation, distance);
    }

    private static void validateIncludeAnyStation(Line line, Station newUpStation, Station newDownStation) {
        if (line.includeAnyStation(newUpStation, newDownStation)) {
            return;
        }

        throw new InvalidSectionException();
    }

    public boolean isBetweenStation(Section newSection) {
        if (upStation.equals(newSection.upStation)) {
            return true;
        }

        if (downStation.equals(newSection.downStation)) {
            return true;
        }

        return false;
    }

    public boolean isShort(Section origin) {
        return distance < origin.distance;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
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

    public boolean matchUpStation(Section newSection) {
        return upStation.equals(newSection.upStation);
    }

    public boolean matchDownStation(Section newSection) {
        return downStation.equals(newSection.downStation);
    }

    public void updateUpStationAndDistance(Section newSection) {
        this.upStation = newSection.downStation;
        this.distance = minusDistance(newSection.distance);
    }

    private Long minusDistance(Long newDistance) {
        return this.distance - newDistance;
    }

    public void updateDownStationAndDistance(Section newSection) {
        this.downStation = newSection.upStation;
        this.distance = minusDistance(newSection.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section that = (Section) o;

        if (!upStation.equals(that.upStation)) {
            return false;
        }

        if (!downStation.equals(that.downStation)) {
            return false;
        }

        return true;
    }
}
