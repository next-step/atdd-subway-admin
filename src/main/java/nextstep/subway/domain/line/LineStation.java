package nextstep.subway.domain.line;

import nextstep.subway.domain.BaseEntity;

import javax.persistence.*;

@Entity
public class LineStation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long upStationId;

    @Column(nullable = false)
    private Long downStationId;

    private int distance;

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    protected LineStation() {
    }

    public LineStation(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void updatePreStationTo(Long downStationId, int distance) {
        this.upStationId = downStationId;
        this.distance -= distance;
    }

    public boolean isSame(LineStation lineStation) {
        return this.getUpStationId() == lineStation.getUpStationId() && this.getDownStationId() == lineStation.getDownStationId();
    }
}
