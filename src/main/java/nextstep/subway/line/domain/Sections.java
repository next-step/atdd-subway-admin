package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections(Section upSection, Section downSection) {
        this.sections.addAll(Arrays.asList(upSection, downSection));
    }

    protected Sections() {
    }

    public List<Station> getStations() {
        return this.getOrderedSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public List<Section> getOrderedSections() {
        Optional<Section> preSection = findFirstSection();
        List<Section> result = new ArrayList<>();
        while (preSection.isPresent()) {
            Section currentSection = preSection.get();
            result.add(currentSection);
            preSection = getNextPreSection(currentSection);
        }
        return result;
    }

    private Optional<Section> findFirstSection() {
        return sections.stream()
                .filter(Section::isFirstSection)
                .findFirst();
    }

    private Optional<Section> getNextPreSection(Section currentSection) {
        return sections.stream()
                .filter(section -> section.matchNextSection(currentSection))
                .findFirst();
    }
}
