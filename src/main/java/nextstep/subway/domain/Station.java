package nextstep.subway.domain;

import javax.persistence.*;

@Entity
@Table(name = "station")
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "line_id")
    private Line line;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        if (this.line != null) {
            this.line.getStations().remove(this);
        }
        this.line = line;
        if (this.line != null && !this.line.getStations().contains(this)) {
            this.line.addStation(this);
        }
    }
}
