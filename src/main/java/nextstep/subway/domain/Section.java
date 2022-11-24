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

    @Embedded
    @AttributeOverride(name = "order", column = @Column(name = "sectionOrder"))
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    public Section(long upStationId, long downStationId, Distance distance, Line line) {
        validSameStationId(downStationId, upStationId);
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
        this.line = line;
    }

    public Section(long upStationId, long downStationId, Distance distance, Order order, Line line) {
        validSameStationId(downStationId, upStationId);
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
        this.order = order;
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

    public boolean isSameDownStation(Section section) {
        return this.downStationId == section.downStationId;
    }

    public boolean isSameUpStation(Section section) {
        return this.upStationId == section.getUpStationId();
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

    public boolean equalsOrder(Section section) {
        return this.order.equals(section.order);
    }

    public boolean isPreOrder(Section section) {
        return this.order.isPreOrder(section.order);
    }

    public boolean isEqualsOrPreOrder(Section section) {
        return this.order.isEqualsOrPreOrder(section.order);
    }

    public boolean isEqualsOrLastOrder(Section section) {
        return this.order.isEqualsOrLastOrder(section.order);
    }

    public int getOrderNumber() {
        return this.order.getOrder();
    }

    public Order getOrder() {
        return order;
    }

    public void updateOrder(Order order) {
        this.order = order;
    }

    public void plusOrder() {
        this.order = order.plusOne();
    }

    private void validSameStationId(long downStationId, long upStationId) {
        if (downStationId == upStationId) {
            throw new IllegalArgumentException("상행역과 하행역은 같은 역일 수 없습니다. 지하철ID:" + downStationId);
        }
    }
}
