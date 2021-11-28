package nextstep.subway.line.domain;

import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.SectionNotFoundException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = LAZY, cascade = ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(List<Section> newSections, Line line) {
        List<Section> addNewSections = newSections.stream()
                                                  .peek(section -> section.setLine(line))
                                                  .collect(toList());
        this.sections.addAll(addNewSections);
    }

    public void add(Section newSection, Line line) {
        if (sections.contains(newSection)) {
            return;
        }
        newSection.setLine(line);
        sections.add(newSection);
    }

    public void addSection(Section newSection) {
        if (matchStation(isDownStation(newSection))) {
            Section parentStation = findParentStation(newSection);
            parentStation.changeUpSection(newSection);
            sections.add(newSection);
        }

        if (matchStation(isUpStation(newSection))) {
            Section parentStation = findNextStation(newSection);
            parentStation.changeDownSection(newSection);
            sections.add(newSection);
        }

    }

    private Section findParentStation(Section newSection) {
        Section parentStation = sections.stream()
                                        .filter(section -> section.equalsParentStation(newSection.getDownStation()))
                                        .findFirst()
                                        .orElseThrow(SectionNotFoundException::new);
        return parentStation;
    }

    private Section findNextStation(Section newSection) {
        Section nextStation = sections.stream()
                                      .filter(section -> section.equalsChildStation(newSection.getUpStation()))
                                      .findFirst()
                                      .orElseThrow(SectionNotFoundException::new);
        return nextStation;
    }

    private boolean matchStation(Predicate<Section> isStation) {
        return sections.stream()
                       .anyMatch(isStation);
    }

    private Predicate<Section> isUpStation(Section newSection) {
        return section -> section.findDownStation(newSection);
    }

    private Predicate<Section> isDownStation(Section newSection) {
        return section -> section.findUpStation(newSection);
    }

    public boolean contains(List<Section> section) {
        return section.contains(section);
    }

    public List<Station> getStations() {
        Optional<Section> findSection = findFirstSection();

        List<Station> resultStations = new ArrayList();

        while (isUntilSorted(findSection)) {
            Section section = findSection.get();
            resultStations.add(section.getUpStation());
            findSection = findNextSection(section);
        }

        return Collections.unmodifiableList(resultStations);
    }

    private boolean isUntilSorted(Optional<Section> section) {
        return section.isPresent();
    }

    public Optional<Section> findNextSection(Section findSection) {
        Station downStation = findSection.getDownStation();
        Optional<Section> nextSection = sections.stream()
                                                .filter(section -> section.equalsNextSection(downStation))
                                                .findFirst();
        return nextSection;
    }

    public Optional<Section> findFirstSection() {
        Optional<Section> findSection = sections.stream()
                                                .filter(SectionType::equalsFirst)
                                                .findFirst();
        return findSection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sections sections1 = (Sections)o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
