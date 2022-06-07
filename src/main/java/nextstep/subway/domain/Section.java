package nextstep.subway.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "line_station")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(nullable = false)
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station station;
    private Integer distance;
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn
    private Section previous;
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn
    private Section next;

    public Section(Line line, Station station, Integer distance, Section previous, Section next) {
        this.line = line;
        this.station = station;
        this.distance = distance;
        this.previous = previous;
        this.next = next;
    }

    protected Section() {
    }

    public Long getId() {
        return id;
    }

    public void updateNext(Section section) {
        this.next = section;
    }

    public void updatePrevious(Section section) {
        this.previous = section;
    }

    public void updateDistance(Integer distance) {
        this.distance = distance;
    }

    public Long getStationId() {
        return this.station.getId();
    }

    public String getName() {
        return this.station.getName();
    }

    public LocalDateTime getCreatedDate() {
        return this.station.getCreatedDate();
    }

    public LocalDateTime getModifiedDate() {
        return this.station.getModifiedDate();
    }

    public Station getStation() {
        return this.station;
    }

    public Section getPrevious() {
        return previous;
    }

    public Section getNext() {
        return next;
    }

    public Integer getDistance() {
        return this.distance;
    }
}
