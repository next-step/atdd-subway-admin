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
    @JoinColumn(name = "previous_station_id")
    private Station previous;

    @ManyToOne
    @JoinColumn(name = "next_station_id")
    private Station next;

    public LineStation() {

    }

    public LineStation(final Line line,
                       final Station station,
                       final Station previous,
                       final Station next) {
        this.line = line;
        this.station = station;
        this.previous = previous;
        this.next = next;
    }

    public void update(final Station previous, final Station next) {
        this.previous = previous;
        this.next = next;
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

    public Station getNext() {
        return next;
    }
}
