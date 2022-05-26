package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.error.CanNotRemovableSectionException;
import nextstep.subway.error.SectionNotFoundException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        addSection(section);
    }

    public Set<Station> getStations() {
        return sections.stream()
            .flatMap(section -> section.getStations().stream())
            .collect(Collectors.toSet());
    }

    private void addSection(Section newSection) {
        checkValidation(newSection);
        sections.forEach(section -> section.update(newSection));
        sections.add(newSection);
    }

    private void checkValidation(Section section) {
        Set<Station> stations = getStations();
        if (isSameStations(section, stations)) {
            throw new IllegalArgumentException("section is already registered.");
        }
        if (isNotContainsStations(section, stations)) {
            throw new IllegalArgumentException("must be one station contains.");
        }
    }

    private boolean isSameStations(Section section, Set<Station> stations) {
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }

    private boolean isNotContainsStations(Section section, Set<Station> stations) {
        return !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation());
    }

    public void remove(Station station) {
        checkValid(station);
        Section firstSection = getFirstSection();
        Section lastSection = getLastSection();
        if (firstSection.isUpStation(station)) {
            this.sections.remove(firstSection);
            return;
        }
        if (lastSection.isDownStation(station)) {
            this.sections.remove(lastSection);
            return;
        }
        removeMiddleSection(station);
    }

    private void checkValid(Station station) {
        if (sections.size() < 2) {
            throw new CanNotRemovableSectionException();
        }
        if (!getStations().contains(station)) {
            throw new CanNotRemovableSectionException();
        }

    }

    private Section getFirstSection() {
        return sections.stream()
            .filter(this::isUp)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("can not find section."));
    }

    private boolean isUp(Section other) {
        return sections.stream()
            .filter(section -> !section.isSame(other))
            .noneMatch(other::isAfter);
    }

    private Section getLastSection() {
        return sections.stream()
            .filter(this::isDown)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("can not find section."));
    }

    private boolean isDown(Section other) {
        return sections.stream()
            .filter(section -> !section.isSame(other))
            .noneMatch(other::isBefore);
    }

    private void removeMiddleSection(Station station) {
        Section beforeSection = getBeforeSection(station);
        Section afterSection = getAfterSection(station);
        addCombinationSection(beforeSection, afterSection);
        removeBeforeAfterSection(beforeSection, afterSection);
    }

    private Section getBeforeSection(Station station) {
        return sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findFirst().orElseThrow(SectionNotFoundException::new);
    }

    private Section getAfterSection(Station station) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findFirst().orElseThrow(SectionNotFoundException::new);
    }

    private void addCombinationSection(final Section beforeSection, final Section afterSection) {
        Section combinedSection = beforeSection.combine(afterSection);
        this.sections.add(combinedSection);
    }

    private void removeBeforeAfterSection(Section beforeSection, Section afterSection) {
        this.sections.remove(beforeSection);
        this.sections.remove(afterSection);
    }
}
