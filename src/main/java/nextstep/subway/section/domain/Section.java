package nextstep.subway.section.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer distance;

    private Integer sortSeq;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    public Section(Integer distance, Integer sortSeq, Station station) {
        this.distance = distance;
        this.sortSeq = sortSeq;
        setStation(station);
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getSortSeq() {
        return sortSeq;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }
}
