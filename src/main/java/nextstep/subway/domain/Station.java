package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
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
        this.line = line;
        if (!line.getStations().contains(this)) {
            line.getStations().add(this);
        }
    }
}
