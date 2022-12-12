package nextstep.subway.domain;

import nextstep.subway.constants.ErrorCode;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Line extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() {}

    public Line(String name, String color) {
        validation(name, color);
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Section section) {
        validation(name, color);
        this.name = name;
        this.color = color;
        addSection(section);
    }

    private void validation(String name, String color) {
        if (name == null || name.isEmpty()) {
            new IllegalArgumentException(ErrorCode.NO_EMPTY_LINE_NAME_EXCEPTION.getErrorMessage());
        }
        if (color == null || color.isEmpty()) {
            new IllegalArgumentException(ErrorCode.NO_EMPTY_LINE_COLOR_EXCEPTION.getErrorMessage());
        }
    }

    public void addSection(Section section) {
        section.belongLine(this);
        this.sections.add(section);
    }

    public void deleteStation(Station requestDeleteStation) {
        this.sections.removeSection(requestDeleteStation);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public List<Station> getStations() {
        return sections.getSortedStations();
    }

    public void modify(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
