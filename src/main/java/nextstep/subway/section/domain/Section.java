package nextstep.subway.section.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validStations(upStation, downStation);

        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public Long getId() {
        return id;
    }

    public List<Station> stations() {
        return Collections.unmodifiableList(Arrays.asList(upStation, downStation));
    }

    public boolean contains(Station station) {
        return stations().contains(station);
    }

    public boolean isUpStationExist(List<Station> stations) {
        return stations.contains(upStation);
    }

    public boolean isDownStationExist(List<Station> stations) {
        return stations.contains(downStation);
    }

    public void splitBy(Section other) {
        distance.subtract(other.distance);

        if (equalUpStation(other.upStation)) {
            upStation = other.downStation;
        }

        if (equalDownStation(other.downStation)) {
            downStation = other.upStation;
        }
    }

    public boolean equalUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean equalDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isDownSectionOf(Section other) {
        return upStation.equals(other.downStation);
    }

    public boolean isUpSectionOf(Section other) {
        return downStation.equals(other.upStation);
    }

    private void validStations(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 같습니다.");
        }
    }

}
