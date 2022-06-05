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

    public LineStation(final Line line, final Station station) {
        this(line, new Section(null, station, new Distance(0L)));
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getPreStation() {
        return this.section.getUpStation();
    }

    public boolean isCurrentStation(final Station station) {
        return Objects.equals(this.section.getDownStation(), station);
    }

    public boolean isStartStation() {
        return Objects.isNull(getPreStation());
    }

    public Distance getDistance() {
        return this.section.getDistance();
    }

    public LineStation addLineStation(final LineStation newLineStation) {
        validationLine(newLineStation);
        return addLineStation(newLineStation, this.section.updatable(newLineStation.section));
    }

    private LineStation addLineStation(LineStation newLineStation, Section updatedSection) {
        if (newLineStation.isSameSection(updatedSection)) {
            return newLineStation;
        }
        return new LineStation(this.line, updatedSection);
    }

    private void validationLine(LineStation newLineStation) {
        if (!Objects.equals(this.line, newLineStation.getLine())) {
            throw new IllegalArgumentException("Line is difference");   
        }
    }

    private boolean isSameSection(final Section updatedSection) {
        return Objects.equals(this.section, updatedSection);
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
