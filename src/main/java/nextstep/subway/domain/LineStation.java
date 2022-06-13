package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "line_station")
public class LineStation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "next_station_id")
    private Station next;

    @Column(name = "distance_to_next_station")
    private Long distanceToNext;

    public LineStation() {

    }

    public LineStation(final Line line,
                       final Station station,
                       final Station next,
                       final Long distanceToNext) {
        this.line = line;
        this.station = station;
        this.next = next;
        this.distanceToNext = distanceToNext;
    }

    public boolean hasNext() {
        return null != next && distanceToNext > 0L;
    }

    public void updateNext(final Station next, final Long distance) {
        this.next = next;
        distanceToNext = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getStation() {
        return station;
    }

    public Station getNext() {
        return next;
    }

    public Long getDistanceToNext() {
        return distanceToNext;
    }
}
