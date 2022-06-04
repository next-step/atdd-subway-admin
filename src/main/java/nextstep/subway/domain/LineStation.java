package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "LINE_ID", nullable = false)
    private Line line;

    @Embedded
    private Section section;

    protected LineStation() {
    }


    public LineStation(final Line line, final Section section) {
        updateLine(line);
        this.section = section;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    private void updateLine(final Line line) {
        if (Objects.isNull(this.line)) {
            this.line = line;
            this.line.getLineStations().add(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStation that = (LineStation) o;
        return Objects.equals(id, that.id) && Objects.equals(line, that.line) && Objects.equals(section, that.section);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, section);
    }
}
