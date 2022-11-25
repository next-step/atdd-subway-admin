package nextstep.subway.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Station downStation;

    @ManyToOne(fetch = FetchType.EAGER)
    private Station upStation;

    @Embedded
    @AttributeOverride(name = "distance", column = @Column(name = "distance"))
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    public Section(Station upStation, Station downStation, Distance distance, Line line) {
        validSameStation(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    private void validSameStation(Station upStation, Station downStationId) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역은 같은 역일 수 없습니다. 지하철ID:" + downStationId);
        }
    }

    protected Section() {

    }

    public Distance getDistance() {
        return distance;
    }

    public Distance minusDistance(Section section) {
        return this.distance.minus(section.getDistance());
    }

    public int getDistanceIntValue() {
        return distance.getDistance();
    }

    public long getDownStationId() {
        return downStation.getId();
    }

    public long getUpStationId() {
        return upStation.getId();
    }

    public void registerLine(Line line) {
        this.line = line;
    }

    public boolean isKnownSection(Section section) {
        return isSameUpStation(section.getUpStation()) ||
                isSameDownStation(section.getDownStation()) ||
                isSameUpStation(section.getDownStation()) ||
                isSameDownStation(section.getUpStation());
    }

    public boolean isKnownStation(Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }

    public boolean isSameUpStation(Station upStation) {
        return this.upStation == upStation;
    }

    public boolean isSameDownStation(Station downStation) {
        return this.downStation == downStation;
    }

    public boolean hasSameDownStation(Section section) {
        return this.downStation == section.downStation;
    }

    public boolean hasSameDownStation(Station station) {
        return this.downStation == station;
    }

    public boolean hasSameUpStation(Section section) {
        return this.upStation == section.upStation;
    }

    public boolean hasSameUpStation(Station station) {
        return this.upStation == station;
    }

    public void updateDistance(Distance distance) {
        this.distance = distance;
    }

    public void updateDownStation(Station station) {
        this.downStation = station;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public void updateUpStation(Station station) {
        this.upStation = station;
    }

    public boolean sameUpStationByDownStation(Section section) {
        return this.upStation.equals(section.downStation);
    }

    public boolean sameDownStationByUpStation(Section section) {
        return this.downStation.equals(section.upStation);
    }
}
