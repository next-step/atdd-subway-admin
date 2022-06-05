package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "line_id")
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;
    private Integer order;

    protected Section() {
    }

    public Section(Long upStationId, Long downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getOrder() {
        return order;
    }

    public void toLine(Line line) {
        this.lineId = line.getId();
    }

    public void updateUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void updateDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void updateDistance(Integer distance) {
        this.distance = distance;
    }

    public void updateOrder(Integer order) {
        this.order = order;
    }

    public void increaseOrder() {
        this.order++;
    }
}
