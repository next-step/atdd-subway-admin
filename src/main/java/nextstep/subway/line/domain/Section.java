package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private SectionDistance distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, SectionDistance distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public Section(Station upStation, Station downStation, SectionDistance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public SectionDistance getDistance() {
        return distance;
    }

    public void add(Line line) {
        this.line = line;
    }

    public boolean isBetweenAndUpStationSameWith(Section oldSection) {
        return upStation.equals(oldSection.getUpStation()) && !downStation.equals(oldSection.getDownStation());
    }

    public boolean isBetweenAndDownStationSameWith(Section oldSection) {
        return downStation.equals(oldSection.getDownStation()) && !upStation.equals(oldSection.getUpStation());
    }

    public boolean isAtTheEndWith(Section oldSection) {
        return downStation.equals(oldSection.getUpStation()) || upStation.equals(oldSection.getDownStation());
    }

    public void updateBy(Station newDownStation, SectionDistance newDistance) {
        updateDownStationTo(newDownStation);
        updateDistanceTo(newDistance);
    }

    private void updateDownStationTo(Station newDownStation) {
        this.downStation = newDownStation;
    }

    private void updateDistanceTo(SectionDistance newDistance) {
        this.distance = newDistance;
    }
}
