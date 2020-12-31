package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void updateUpStation(Station upStation, int distance) {
        this.upStation = upStation;
        this.distance = distance;
    }

    public void updateDownStation(Station downStation, int distance) {
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean equalStations(Section otherSection) {
        return equalUpUpStation(otherSection) && equalDownDownStation(otherSection);
    }

    public boolean equalUpUpStation(Section otherSection) {
        return upStation.equals(otherSection.getUpStation());
    }

    public boolean equalDownDownStation(Section otherSection) {
        return downStation.equals(otherSection.getDownStation());
    }

    public boolean equalUpDownStation(Section otherSection) {
        return upStation.equals(otherSection.getDownStation());
    }

    public boolean equalDownUpStation(Section otherSection) {
        return downStation.equals(otherSection.getUpStation());
    }

    public boolean isEqualOrMoreDistance(Section otherSection) {
        return distance >= otherSection.getDistance();
    }

    public int minusDistance(Section otherSection) {
        return distance - otherSection.getDistance();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
