package nextstep.subway.domain;

import nextstep.subway.exception.LineException;

import javax.persistence.*;
import java.util.List;

import static nextstep.subway.exception.LineExceptionMessage.EMPTY_LINE_NAME;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        validateLine(name);
        this.name = name;
        this.color = color;
    }

    private void validateLine(String name) {
        if (name == null || name.isEmpty()) {
            throw new LineException(EMPTY_LINE_NAME.getMessage());
        }
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

    public void modifyLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void deleteSection(Station deleteStation) {
        sections.deleteSection(deleteStation);
    }

}
