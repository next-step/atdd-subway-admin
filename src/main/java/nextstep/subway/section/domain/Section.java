package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private Line line;

    private int distance;

    protected Section() {
    }

    public Section(int distance) {
        this.distance = distance;
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance;
    }

    public Long getId() {
        return id;
    }

    public void addLine(Line line) {
        this.line = line;
    }
}
