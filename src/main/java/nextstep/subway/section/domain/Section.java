package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @ManyToOne
    private Station upStation;
    @ManyToOne
    private Station downStation;

    @Column(nullable = false)
    private Distance distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        validateLine(line);
        validateStation(upStation, downStation);

        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateLine(Line line) {
        if (Objects.isNull(line)) {
            throw new IllegalStateException("Line은 필수입니다.");
        }
    }

    private void validateStation(Station upStation, Station downStation) {
        if (Objects.isNull(upStation)) {
            throw new IllegalStateException("upStation은 필수입니다.");
        }
        if (Objects.isNull(downStation)) {
            throw new IllegalStateException("downStation은 필수입니다.");
        }
    }

    public void resizeAndChangeNearStation(Section section) {
        changeNearStation(section);
        this.distance = distance.minus(section.distance);
    }

    private void changeNearStation(Section section) {
        if (isSameDownStation(section)) {
            this.downStation = section.upStation;
        } else if (isSameUpStation(section)) {
            this.upStation = section.downStation;
        }
    }

    public Long getId() {
        return id;
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

    public boolean isUpStationBetween(Station station, Distance distance) {
        return isUpStation(station) &&
                isDistanceUnder(distance);
    }

    public boolean isDownStationBetween(Station station, Distance distance) {
        return isDownStation(station) &&
                isDistanceUnder(distance);
    }

    public boolean isLower(Section section) {
        return isUpStation(section.downStation);
    }

    public boolean isUpper(Section section) {
        return isDownStation(section.upStation);
    }

    public boolean isContains(Station station) {
        return isDownStation(station) || isUpStation(station);
    }

    public boolean isDistanceUnder(Distance distance) {
        return this.distance.isLessThan(distance);
    }

    public boolean isSameUpStation(Section section) {
        return upStation == section.getUpStation();
    }

    public boolean isSameDownStation(Section section) {
        return downStation == section.getDownStation();
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(Arrays.asList(upStation, downStation));
    }

    private boolean isDownStation(Station station) {
        return downStation == station;
    }

    private boolean isUpStation(Station station) {
        return upStation == station;
    }

}
