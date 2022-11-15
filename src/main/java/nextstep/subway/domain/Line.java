package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @Embedded
    Sections sections = new Sections();

    public static Line of(String name, String color, Station upStation, Station downStation, Integer distance) {
        Line line = new Line(name, color);
        line.addInitialSections(Section.makeInitialSections(upStation, downStation, distance));
        return line;
    }

    protected Line() {

    }

    public Line(String name, String color) {
        validName(name);
        validColor(color);

        this.name = name;
        this.color = color;
    }

    public List<Station> getStations() {
        return sections.getStations();
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

    public void changeNameAndColor(String newName, String newColor) {
        this.name = newName;
        this.color = newColor;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void insertNewSection(Station upStation, Station downStation, Integer distance) {
        sections.insertNewSection(upStation, downStation, distance, this);

    }

    public void addSection(Section section) {
        sections.addSection(section, this);
    }

    private void addInitialSections(List<Section> sections) {
        sections.forEach(this::addSection);
    }

    private static void validColor(String color) {
        if (StringUtils.isBlank(color)) {
            throw new IllegalArgumentException("노선의 색을 입력하세요.");
        }
    }

    private static void validName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("노선의 이름을 입력하세요.");
        }
    }
}
