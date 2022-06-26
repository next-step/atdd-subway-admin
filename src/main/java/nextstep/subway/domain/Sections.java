package nextstep.subway.domain;

import nextstep.subway.exception.DuplicatedSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section newSection) {
        if (this.sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        validateUnique(newSection);
        validateDistance(newSection);
        sections.add(newSection);
    }

    private void validateUnique(Section newSection) {
        if (sections.contains(newSection)) {
            throw new DuplicatedSectionException(newSection.getUpStation().getId(), newSection.getDownStation().getId());
        }
    }

    public List<Station> findStations() {
        return this.sections.stream()
            .map(Section::getStations)
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void validateDistance(Section newSection) {
        Section connectedSection = findConnectedSection(newSection);
        if (connectedSection == null) {
            return;
        }

        connectedSection.validateDistanceAndUpdateSection(newSection);
    }

    private Section findConnectedSection(Section newSection) {
        return this.sections.stream()
            .filter(section -> section.isBetweenStation(newSection))
            .findFirst()
            .orElse(null);
    }
}
