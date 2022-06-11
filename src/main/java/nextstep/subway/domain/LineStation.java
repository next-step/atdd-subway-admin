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

    // TODO : 래팩토링 후 미사용 시 제거
    @ManyToOne
    @JoinColumn(name = "previous_station_id")
    private Station previous;

    // TODO : 래팩토링 후 미사용 시 제거
    @Column(name = "distance_to_previous_station")
    private Long distanceToPrevious;

    @ManyToOne
    @JoinColumn(name = "next_station_id")
    private Station next;

    @Column(name = "distance_to_next_station")
    private Long distanceToNext;

    public LineStation() {

    }

    public LineStation(final Line line,
                       final Station station,
                       final Station previous,
                       final Long distanceToPrevious,
                       final Station next,
                       final Long distanceToNext) {
        this.line = line;
        this.station = station;
        this.previous = previous;
        this.distanceToPrevious = distanceToPrevious;
        this.next = next;
        this.distanceToNext = distanceToNext;
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

    public void updatePrevious(final Station previous, final Long distance) {
        this.previous = previous;
        distanceToPrevious = distance;
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

    public Station getPrevious() {
        return previous;
    }

    public Long getDistanceToPrevious() {
        return distanceToPrevious;
    }

    public Station getNext() {
        return next;
    }

    public Long getDistanceToNext() {
        return distanceToNext;
    }
}
