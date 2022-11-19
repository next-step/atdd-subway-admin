package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preStationId")
    private Station preStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stationId")
    private Station station;

    private int distance;

    protected Section() {}

    public Section(Station station) {
        this.station = station;
    }

    public Section(Station preStation, Station station, int distance) {
        this.preStation = preStation;
        this.station = station;
        this.distance = distance;
    }

    public void resetStation(Station station) {
        this.station = station;
    }

    public void resetPreStation(Station station) {
        this.preStation = station;
    }

    public boolean isLessThanDistance(Section infixSection) {
        if (this.distance == 0) {
            return false;
        }
        return infixSection.distance >= this.distance;
    }

    public boolean isEqualsId(Long id) {
        return station.getId().equals(id);
    }

    public Station getStation() {
        return station;
    }

    public Station getPreStation() {
        return preStation;
    }

}
