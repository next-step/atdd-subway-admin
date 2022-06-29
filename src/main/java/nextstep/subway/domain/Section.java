package nextstep.subway.domain;

import nextstep.subway.exception.InvalidDistanceException;
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
    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, Long distance) {
        validateIncludeAnyStation(line, upStation, downStation);
        return new Section(line, upStation, downStation, new Distance(distance));
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

        return downStation.equals(newSection.downStation);
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

    public Distance getDistance() {
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
        this.distance = distance.minus(newSection.distance);
    }

    public void updateDownStationAndDistance(Section newSection) {
        this.downStation = newSection.upStation;
        this.distance = distance.minus(newSection.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section that = (Section) o;

        if (!upStation.equals(that.upStation)) {
            return false;
        }

        return downStation.equals(that.downStation);
    }

    public void updateSection(Section newSection) {
        if (matchUpStation(newSection)) {
            updateUpStationAndDistance(newSection);
            return;
        }

        if (matchDownStation(newSection)) {
            updateDownStationAndDistance(newSection);
            return;
        }
    }

    public void validateDistanceAndUpdateSection(Section newSection) {
        validateDistance(newSection.distance);
        updateSection(newSection);
    }

    private void validateDistance(Distance target) {
        if (!distance.isLong(target)) {
            throw new InvalidDistanceException(target);
        }
    }

    public void merge(Section target) {
        this.downStation = target.downStation;
        this.distance = distance.plus(target.distance);
    }
}
