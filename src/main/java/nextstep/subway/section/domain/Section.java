package nextstep.subway.section.domain;

import nextstep.subway.common.entity.BaseEntity;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

/**
 * packageName : nextstep.subway.section
 * fileName : Section
 * author : haedoang
 * date : 2021/11/20
 * description : 구간 엔티티
 */
@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int distance;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "STATION_ID")
    private Station station;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "NEXT_STATION_ID")
    private Station nextStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "LINE_ID")
    private Line line;


    protected Section() {
    }

    public Section(Line line, Station station, Station nextStation, int distance) {
        if (Objects.isNull(line)) {
            throw new NotFoundException("노선이 존재하지 않습니다.");
        }

        if (Objects.isNull(station) || Objects.isNull(nextStation)) {
            throw new NotFoundException("역이 존재하지 않습니다.");
        }
        this.line = line;
        this.station = station;
        this.nextStation = nextStation;
        this.distance = distance;
    }

    public Section(int distance, Station station, Station nextStation) {
        this.distance = distance;
        this.station = station;
        this.nextStation = nextStation;
    }

    public Section addLine(Line line) {
        this.line = line;
        return this;
    }
    
    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public Station getStation() {
        return station;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Line getLine() {
        return line;
    }


}
