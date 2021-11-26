package nextstep.subway.line.domain;


import java.security.InvalidParameterException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.Where;

@Entity
@Where(clause = "deleted = false")
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long stationId;

    private Long nextStationId;

    private Integer distance;

    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    protected LineStation() {
    }

    public LineStation(Long stationId, Long nextStationId, Integer distance) {
        this.stationId = stationId;
        this.nextStationId = nextStationId;
        this.distance = distance;
    }

    public static LineStation lastOf(LineStation lineStation) {
        return new LineStation(lineStation.getNextStationId(), null, lineStation.getDistance());
    }

    public static LineStation of(Long stationId, Long nextStationId, int distance) {
        return new LineStation(stationId, nextStationId, distance);
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void stationIdUpdate(LineStation lineStation) {
        if (Objects.equals(this.stationId, lineStation.stationId)) {
            this.stationId = lineStation.nextStationId;
        }
    }

    public void nextStationIdUpdate(LineStation lineStation) {
        if (this.distance <= lineStation.distance) {
            throw new InvalidParameterException("추가역은 기존 구간길이 보다 미만이어야 합니다.");
        }
        this.nextStationId = lineStation.stationId;
    }

    public boolean isNext(LineStation lineStation) {
        return this.getNextStationId().equals(lineStation.nextStationId);
    }

    public boolean isLast() {
        return Objects.isNull(nextStationId);
    }

    public boolean isAddableMatch(LineStation lineStation) {
        return this.stationId.equals(lineStation.nextStationId)
            || this.stationId.equals(lineStation.stationId)
            || this.nextStationId.equals(lineStation.stationId);
    }

    public boolean isDuplicate(LineStation lineStation) {
        boolean isStationE = this.stationId.equals(lineStation.stationId);
        boolean isNextStationE = this.nextStationId.equals(
            lineStation.nextStationId);
        return (isStationE && isNextStationE);
    }


    public Long getStationId() {
        return stationId;
    }

    public Long getNextStationId() {
        return nextStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (Objects.isNull(id)) {
            return false;
        }

        LineStation lineStation = (LineStation) o;
        return Objects.equals(id, lineStation.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
