package nextstep.subway.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.Hibernate;

@Entity
public class LineStation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long stationId;
    private long preStationId;
    private int distance;

    @ManyToOne( fetch = FetchType.LAZY)
    private Line line;

    public LineStation() {
    }

    public LineStation(long stationId, long preStationId, int distance) {
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
    }

    public LineStation(long stationId, long preStationId, int distance, Line line) {
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
        this.line = line;
    }

    public LineStation(Long id, long stationId, long preStationId, int distance) {
        this.id = id;
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
    }

    public LineStation(Long id, long stationId, long preStationId, int distance, Line line) {
        this.id = id;
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }
    public void setLine(Line line) {
        this.line = line;
    }

    public long getStationId() {
        return stationId;
    }

    public long getPreStationId() {
        return preStationId;
    }

    public void updateUpStationTo(Long stationId) {
        this.preStationId = stationId;
    }

    public void minusDistace(int distance) {
        if(this.distance <= distance){
            throw new RuntimeException("등록하려는 역 길이기 기존 역길이 보다 깁니다");
        }
        this.distance -= distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        LineStation that = (LineStation) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
