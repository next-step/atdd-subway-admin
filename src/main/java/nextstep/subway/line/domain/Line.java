package nextstep.subway.line.domain;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections;

    protected Line() {}

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public static Line of(String name, String color, Section section) {
        Line line = new Line(name, color);
        line.addSection(section);
        return line;
    }

    public void addSection(Section section) {
        this.sections.addSection(section);
        section.addLine(this);
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
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

    public void remove(Station station) {
        if (sections.hasNot(station)) {
            throw new IllegalArgumentException("노선에 등록되어 있지 않은 역은 제거할 수 없습니다.");
        }
        sections.remove(station);
    }
}
