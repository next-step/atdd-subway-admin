package nextstep.subway.line.domain;


import javax.persistence.*;
import java.util.Objects;

@Table
@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long upStationId;

    @Column(nullable = false)
    private Long stationId;

    private int distance;

    protected LineStation(){}

    public LineStation(Long upStationId, Long stationId, int distance){
        this.upStationId = upStationId;
        this.stationId = stationId;
        this.distance = distance;
    }

    public static LineStation ofFirst(Long stationId){
        return new LineStation(null, stationId, 0);
    }

    public static LineStation of(Long upStationId, Long stationId, int distance){
        if(distance < 0){
            throw new IllegalArgumentException("구간거리는 0보다 작을 수 없습니다.");
        }
        return new LineStation(upStationId, stationId, distance);
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public boolean hasSameUpStation(Long upStationId){
       return Objects.equals(this.upStationId, upStationId);
    }

    public boolean isSameStation(long stationId){
        return this.stationId == stationId;
    }

    public void changeUpStation(Long id, int changedDistance){
        this.upStationId = id;
        if(id == null){
            this.distance = 0;
        }else {
            this.distance = this.distance + changedDistance;
        }
    }

    public boolean isFirst(){
        return this.upStationId == null;
    }
}
