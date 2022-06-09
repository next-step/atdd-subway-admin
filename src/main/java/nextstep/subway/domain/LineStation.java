package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "LINESTATION")
public class LineStation extends BaseEntity {

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
        this.section = section;
        addLine(line);
    }

    public LineStation(final Line line, final Station station) {
        this(line, new Section(null, station, new Distance(0)));
    }

    private void addLine(final Line line) {
        this.line = line;
        this.line.addLineStation(this);
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

    public Station getCurrentStation() {
        return this.section.getDownStation();
    }

    public boolean isCurrentStation(final Station station) {
        return Objects.equals(this.section.getDownStation(), station);
    }

    public boolean isStartStation() {
        return Objects.equals(getPreStation(), null);
    }

    public Distance getDistance() {
        return this.section.getDistance();
    }

    public boolean isPreStation(Station station) {
        return Objects.equals(this.getPreStation(), station);
    }

    public LineStation updateLineStation(final LineStation newLineStation) {
        validationLine(newLineStation);
        Section changedSection = this.section.updatable(newLineStation.getSection());
        if (!changedSection.isSameSection(newLineStation.getSection())) {
            updateBySection(getSectionBy(this.getSection(), newLineStation.getSection()));
        }
        return newLineStation;
    }

    public void updateLineBy(final Line line) {
        this.line = line;
    }

    public Section getSection() {
        return section;
    }

    public void updateBySection(final Section section) {
        this.section.updateUpStationBy(section.getUpStation())
                .updateDownStationBy(section.getDownStation())
                .updateDistanceBy(section.getDistance());
    }

    private Section getSectionBy( final Section source, final Section destination) {
        final Distance updatedDistance = source.getDistance().subtract(destination.getDistance());
        return Objects.equals(source.getUpStation(), destination.getUpStation()) ?
                new Section(destination.getDownStation(), source.getDownStation(), updatedDistance) :
                new Section(source.getUpStation(), destination.getUpStation(), updatedDistance);
    }

    private void validationLine(LineStation newLineStation) {
        if (Objects.isNull(newLineStation)) {
            throw new IllegalArgumentException("invalid Parameter");
        }
        if (!Objects.equals(this.line, newLineStation.getLine())) {
            throw new IllegalArgumentException("Line is difference");
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
