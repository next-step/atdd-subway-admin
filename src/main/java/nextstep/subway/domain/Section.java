package nextstep.subway.domain;

import javax.persistence.*;

@Entity
@Table(name = "section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Section parent = null;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @ManyToOne(fetch = FetchType.EAGER)
    private Station station;

    private Long distance = 0L;

    public Section() {
    }

    public Section(Line line, Station station) {
        this.line = line;
        this.station = station;
    }

    public Section(Section parent, Line line, Station station, Long distance) {
        this.parent = parent;
        this.line = line;
        this.station = station;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Section getParent() {
        return parent;
    }

    public void setParent(Section parent) {
        this.parent = parent;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        if (this.line != null) {
            this.line.getSections().remove(this);
        }
        this.line = line;
        if (this.line != null && !this.line.getSections().contains(this)) {
            this.line.addSection(this);
        }
    }

    public Station getStation() {
        return station;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }
}
