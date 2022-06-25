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
    private List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section newSection) {
        validateUnique(newSection);
        sections.add(newSection);
    }

    private void validateUnique(Section newSection) {
        if (sections.contains(newSection)) {
            throw new DuplicatedSectionException(newSection.getUpStation().getId(), newSection.getDownStation().getId());
        }
    }

    public boolean isNotEmpty() {
        return !this.sections.isEmpty();
    }

    public List<Station> findStations() {
        return this.sections.stream()
            .map(Section::getStations)
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());
    }
}
