package nextstep.subway.line.domain;


import javax.persistence.*;

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

    public static LineStation ofFirst(long stationId){
        return new LineStation(null, stationId, 0);
    }

    public static LineStation of(long upStationId, long stationId, int distance){
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

    public boolean hasSameUpStation(long upStationId){
       return this.upStationId == upStationId;
    }

    public void changeUpStation(long id){
        this.upStationId = id;
    }
}
