package nextstep.subway.line.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table
public class LineStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long stationId;
    private Long preStationId;
    private Integer distance;

    protected LineStation() {
    }

    public LineStation(Long stationId, Long preStationId, Integer distance) {
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
    }

    public boolean isSame(LineStation newLineStation) {
        return Objects.equals(this.stationId, newLineStation.stationId);
    }

    public Long getId() {
        return id;
    }

    public Long getStationId() {
        return stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public void updatePreStationTo(Long newPreStationId) {
        this.preStationId = newPreStationId;
    }

    public void updateDistance(Integer distance) {
        this.distance = distance;
    }
}
