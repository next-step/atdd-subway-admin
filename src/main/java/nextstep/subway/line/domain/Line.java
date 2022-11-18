package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.LineExceptionCode;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        validateName(name);
        validateColor(color);
        this.name = name;
        this.color = color;
    }

    private void validateName(String name) {
        if(Objects.isNull(name)) {
            throw new IllegalArgumentException(LineExceptionCode.REQUIRED_NAME.getMessage());
        }
    }

    private void validateColor(String color) {
        if(Objects.isNull(color)) {
            throw new IllegalArgumentException(LineExceptionCode.REQUIRED_COLOR.getMessage());
        }
    }

    public void addSection(Section section) {
        sections.addSection(this, section);
    }

    public void update(String name, String color) {
        validateName(name);
        validateColor(color);
        this.name = name;
        this.color = color;
    }

    public void updateSections(Section request, List<Section> matchedSections) {
        sections.updateSections(this, request, matchedSections);
    }

    public void deleteSectionContainsStation(Optional<Section> sectionOfUpStation,
            Optional<Section> sectionOfDownStation) {
        sections.deleteSectionContainsStation(this, sectionOfUpStation, sectionOfDownStation);
    }

    void deleteSection(Section request) {
        sections.deleteSection(request);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
