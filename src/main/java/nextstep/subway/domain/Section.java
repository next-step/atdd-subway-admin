package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.springframework.dao.DataIntegrityViolationException;

@Entity
public class Section extends BaseEntity {
    public static final String ERROR_DISTANCE_OVER = "중간 구간의 길이가 상위 구간의 길이보다 길거나 같습니다.";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "line_id")
    private Long lineId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    private Integer distance;
    private Integer order;

    protected Section() {
    }

    public Section(Long lineId, Station upStation, Station downStation, Integer distance) {
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean match(Section section) {
        if (this.upStation.equals(section.upStation) || this.downStation.equals(section.downStation)) {
            validateDistanceOver(section);
            return true;
        }

        return false;
    }

    private void validateDistanceOver(Section section) {
        if (distance <= section.distance) {
            throw new DataIntegrityViolationException(ERROR_DISTANCE_OVER);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getOrder() {
        return order;
    }

    public void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void updateDownStation(Station downStation) {
        this.downStation = downStation;
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
