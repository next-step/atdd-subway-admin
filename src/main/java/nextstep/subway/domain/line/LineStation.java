package nextstep.subway.domain.line;

import nextstep.subway.domain.BaseEntity;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.request.LineRequest;

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

//    public static LineStation of(LineRequest lineRequest) {
//
//
//    }


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

    protected LineStation() {}

    public LineStation(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
