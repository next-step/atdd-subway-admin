package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @EmbeddedId
    private SectionPK id = new SectionPK();
    private int distance;
    private int orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lineId")
    @JoinColumn(name = "lineId", insertable = false, updatable = false)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("upStationId")
    @JoinColumn(name = "upStationId", insertable = false, updatable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("downStationId")
    @JoinColumn(name = "downStationId", insertable = false, updatable = false)
    private Station downStation;

    public Section() {
    }

    public Section(int distance, int orderId, Station upStation, Station downStation) {
        this.distance = distance;
        this.orderId = orderId;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public SectionPK getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public int getOrderId() {
        return orderId;
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

    public void setLine(Line line) {
        this.line = line;
    }
}
