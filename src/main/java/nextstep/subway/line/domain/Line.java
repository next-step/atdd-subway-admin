package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        sections = new Sections();
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

    public void sortSections() {
        List<Station> upStations = sections.getUpStations();
        List<Station> downStations = sections.getDownStations();

        Station upStation = upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst().get();

        List<Section> newSections = addNewSections(upStation);
        sections.updateSections(newSections);
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void createSection(Section section) {
        sections.addInitSection(section);
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    private List<Section> addNewSections(Station upStation) {
        List<Section> newSections = new ArrayList<>();
        Section firtSection = sections.getSections().stream()
                .filter(section -> section.getUpStation() == upStation)
                .findFirst().get();

        newSections.add(firtSection);

        Station downStation = firtSection.getDownStation();

        while (!(sections.getSections().size() == newSections.size())) {
            Station finalDownStation = downStation;
            Optional<Section> optionalSection = sections.getSections().stream()
                    .filter(section -> section.getUpStation() == finalDownStation)
                    .findFirst();

            if (optionalSection.isPresent()) {
                newSections.add(optionalSection.get());
                downStation = optionalSection.get().getUpStation();
            }
        }
        return newSections;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        return id != null ? id.equals(line.id) : line.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
