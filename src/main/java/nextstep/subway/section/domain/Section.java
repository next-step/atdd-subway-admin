package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.line.domain.Line;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Entity
public class Section {
    public static final String SECTION_DISTANCE_MINUS_ERROR_MSG = "중간에 포함되는 구간이 길이가 감싸고 있는 구간보다 작아야합니다.";

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
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public void updateUpSection(Section newSection) {
        try {
            distance.minus(newSection.getDistance());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(SECTION_DISTANCE_MINUS_ERROR_MSG);
        }
        this.upStation = newSection.getDownStation();
    }

    public void updateDownSection(Section newSection) {
        try {
            distance.minus(newSection.getDistance());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(SECTION_DISTANCE_MINUS_ERROR_MSG);
        }

        this.downStation = newSection.getUpStation();
    }

    public boolean getEqualsUpStation(Station downStation) {
        return this.upStation.equals(downStation);
    }

    public boolean isUpAndDownStationContains (Set<Station> stations) {
        return stations.containsAll(Arrays.asList(upStation, downStation));
    }

    public boolean isUpAndDownStationNotContains (Set<Station> stations) {
        return !stations.contains(upStation) && !stations.contains(downStation);
    }

    public void setLine(Line line) {
        this.line = line;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
