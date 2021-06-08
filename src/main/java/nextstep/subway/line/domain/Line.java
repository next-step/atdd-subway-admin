package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.ValueFormatException;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.domain.Station;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.util.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections(new HashSet<>());

    protected Line() {
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    public static Line create(String name, String color) {
        validate(name, color);
        return new Line(name, color);
    }

    public static Line create(String name, String color, Station upStation, Station downStation, int distance) {
        Line line = Line.create(name, color);
        Section.create(line, upStation, downStation, distance);
        return line;
    }

    private static void validate(String name, String color) {
        if (Strings.isBlank(name)) {
            throw new ValueFormatException("노선의 이름이 존재하지 않습니다.", "name", name, null);
        }

        if (Strings.isBlank(color)) {
            throw new ValueFormatException("노선의 색이 존재하지 않습니다.", "color", color, null);
        }
    }

    public void change(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Station upEndStation() {
        return sections().upEndStation();
    }

    public Station downEndStation() {
        return sections().downEndStation();
    }

    public void addSections(Section section) {
        sections.add(section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int sectionsSize() {
        return sections.size();
    }

    public Sections sections() {
        return sections;
    }

    public List<Station> stationsFromUpToDown() {
        return sections.stationsFromUpToDown();
    }
}
