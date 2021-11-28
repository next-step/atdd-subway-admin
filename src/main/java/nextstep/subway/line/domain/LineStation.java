package nextstep.subway.line.domain;


import java.security.InvalidParameterException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
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

    @Embedded
    private Distance distance;

    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    protected LineStation() {
    }

    public LineStation(Long stationId, Long nextStationId, Distance distance) {
        validateNotNull(stationId);

        this.stationId = stationId;
        this.nextStationId = nextStationId;
        this.distance = distance;
    }

    public static LineStation lastOf(LineStation lineStation) {
        return new LineStation(lineStation.getNextStationId(), null, new Distance(0));
    }

    public static LineStation of(Long stationId, Long nextStationId, Distance distance) {
        return new LineStation(stationId, nextStationId, distance);
    }

    public void lastLineStationUpdate(LineStation lineStation) {
        if (Objects.equals(this.stationId, lineStation.stationId)) {
            this.stationId = lineStation.nextStationId;
        }
    }

    public void pushNextStationId(LineStation lineStation) {
        distance.minus(lineStation.getDistance());

        this.nextStationId = lineStation.stationId;
    }

    public void relocationNextStationId(LineStation lineStation) {
        this.nextStationId = lineStation.nextStationId;

        if (lineStation.isLast()) {
            distance.zero();
            return;
        }

        distance.plus(lineStation.getDistance());
    }

    public boolean isSameNextStationId(LineStation lineStation) {
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
        return stationId.equals(lineStation.stationId)
            && nextStationId.equals(lineStation.nextStationId);
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isSameStationId(Long stationId) {
        return this.stationId.equals(stationId);
    }

    public boolean isPre(LineStation lineStation) {
        if (Objects.isNull(this.nextStationId)) {
            return false;
        }
        return this.nextStationId.equals(lineStation.getStationId());
    }

    public Long getStationId() {
        return stationId;
    }

    public Long getNextStationId() {
        return nextStationId;
    }

    public Distance getDistance() {
        return distance;
    }

    public int getDistanceValue() {
        return distance.getDistance();
    }

    private void validateNotNull(Long stationId) {
        if (Objects.isNull(stationId)) {
            throw new InvalidParameterException("상행 지하철역은 필수 정보 입니다.");
        }
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
