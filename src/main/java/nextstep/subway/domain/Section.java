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

    @Column(name = "downStationId")
    private long downStationId;

    @Column(name = "upStationId")
    private long upStationId;

    @Embedded
    @AttributeOverride(name = "distance", column = @Column(name = "distance"))
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    public Section(long downStationId, long upStationId, Distance distance, Line line) {
        validSameStationId(downStationId, upStationId);
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
        this.line = line;
    }

    public Section(Line line) {
        validSameStationId(line.getDownStationId(), line.getUpStationId());
        this.downStationId = line.getDownStationId();
        this.upStationId = line.getUpStationId();
        this.distance = line.getDistance();
        this.line = line;
    }

    protected Section() {

    }

    public Distance getDistance() {
        return distance;
    }

    public Distance minusDistance(Distance distance) {
        return this.distance.minus(distance);
    }

    public int getDistanceIntValue() {
        return distance.getDistance();
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public void registerLine(Line line) {
        this.line = line;
        refreshDownUp();
    }

    public boolean isKnownSection(Section section) {
        return isSameUpStationId(section.getUpStationId()) ||
                isSameDownStationId(section.getDownStationId()) ||
                isSameUpStationId(section.getDownStationId()) ||
                isSameDownStationId(section.getUpStationId());
    }

    public boolean isKnownStationId(long stationId) {
        return this.upStationId == stationId || this.downStationId == stationId;
    }

    public boolean isSameUpStationId(long upStationId) {
        return this.upStationId == upStationId;
    }

    public boolean isSameDownStationId(long downStationId) {
        return this.downStationId == downStationId;
    }

    public boolean isSameUpStation(Section section) {
        return this.upStationId == section.getUpStationId();
    }

    public boolean isSameDownStation(Section section) {
        return this.downStationId == section.getDownStationId();
    }

    public boolean distanceEquealsOrGreaterThan(Section streamSection) {
        return this.distance.equalsOrGreaterThan(streamSection.getDistance());
    }

    public void updateDistance(Distance distance) {
        this.distance = distance;
    }

    public void updateDownStationId(long stationId) {
        this.downStationId = stationId;
    }

    public void updateUpStationId(long stationId) {
        this.upStationId = stationId;
    }

    private void refreshDownUp() {
        line.updateUpstationId(this.downStationId);
        line.updateDownStationId(this.upStationId);
    }

    private void validSameStationId(long downStationId, long upStationId) {
        if (downStationId == upStationId) {
            throw new IllegalArgumentException("상행역과 하행역은 같은 역일 수 없습니다. 지하철ID:" + downStationId);
        }
    }
}
