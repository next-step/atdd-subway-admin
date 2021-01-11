package nextstep.subway.line.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
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

    public void sortSections() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        Station upStation = upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst().get();

        List<Section> newSections = addNewSections(upStation);
        updateSections(newSections);
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void createSection(Section section) {
        this.sections.createSection(section);
    }

    public void addSection(Section newSection) {
        this.sections.addSection(newSection);
    }


    private List<Station> getDownStations() {
        List<Station> downStations = sections.getSections().stream()
                .map(section -> section.getDownStation())
                .collect(Collectors.toList());
        return downStations;
    }

    private List<Station> getUpStations() {
        List<Station> upStations = sections.getSections().stream()
                .map(section -> section.getUpStation())
                .collect(Collectors.toList());
        return upStations;
    }

    private List<Section> addNewSections(Station upStation) {
        List<Section> newSections = new ArrayList<>();
        Section firtSection = sections.getSections().stream()
                .filter(section -> section.getUpStation() == upStation)
                .findFirst().get();

        newSections.add(new Section(firtSection));

        Station downStation = firtSection.getDownStation();
        int size = sections.getSections().size();

        while (newSections.size() != size) {
            Station finalDownStation = downStation;
            Optional<Section> optionalSection = sections.getSections().stream()
                    .filter(section -> section.getUpStation() == finalDownStation)
                    .findFirst();

            if (optionalSection.isPresent()) {
                newSections.add(new Section(optionalSection.get()));
                downStation = optionalSection.get().getUpStation();
            }
        }
        return newSections;
    }

    private void updateSections(List<Section> newSections) {
        this.sections.updateSections(newSections);
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
