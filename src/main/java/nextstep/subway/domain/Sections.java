package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.dto.StationResponse;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public void addSection(Section section) {
        this.sections.forEach(it -> it.update(section));
        this.sections.add(section);
    }

    public List<StationResponse> toInOrderStationResponse() {
        return getInOrderStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    LinkedHashSet<Station> getInOrderStations() {
        return getInOrderSections().stream()
                .map(Section::stations)
                .flatMap(List::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    List<Section> getInOrderSections() {
        List<Section> inOrderStation = new ArrayList<>();
        Section section = lineFirstSection();

        while (section != null) {
            inOrderStation.add(section);
            section = findNextSection(section);
        }
        return inOrderStation;
    }

    private Section lineFirstSection() {
        Section section = this.sections.get(size() - 1);
        while (findPreviousSection(section) != null) {
            section = findPreviousSection(section);
        }
        return section;
    }

    private Section findPreviousSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.downStation().equals(section.upStation()))
                .findFirst()
                .orElse(null);
    }

    private Section findNextSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.upStation().equals(section.downStation()))
                .findFirst()
                .orElse(null);
    }

    public int size() {
        return this.sections.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
