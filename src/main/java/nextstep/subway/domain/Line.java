package nextstep.subway.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    private static final int MIN_SECTIONS_SIZE = 1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    protected Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections.add(section);
        section.setLine(this);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, Section.of(upStation, downStation, distance));
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

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.add(section);
        section.addLine(this);
    }

    public void remove(Station station) {
        if (!sections.isContain(station)) {
            throw new IllegalArgumentException("노선에 없는 역입니다.");
        }
        if (sections.getSections().size() <= MIN_SECTIONS_SIZE) {
            throw new IllegalArgumentException("구간이 1개이면 역을 제거할 수 없습니다.");
        }
        sections.remove(station);
    }
}
