package nextstep.subway.line.domain;

import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
            addUpSectionOrMiddle(newSection);
            return;
        }

        if (matchStation(isUpStation(newSection))) {
            addDownSection(newSection);
            return;
        }
    }

    private boolean matchStation(Predicate<Section> isStation) {
        return sections.stream()
                       .anyMatch(isStation);
    }

    private Predicate<Section> isDownStation(Section newSection) {
        return section -> section.findUpStation(newSection);
    }

    private void addUpSectionOrMiddle(Section newSection) {
        Section parentStation = findSections(conditionPreviousSection(newSection));
        parentStation.changeUpSection(newSection);
        sections.add(newSection);
    }

    private Predicate<Section> conditionPreviousSection(Section newSection) {
        return section -> section.equalsPreviousUpStation(newSection);
    }


    private Predicate<Section> isUpStation(Section newSection) {
        return section -> section.equalsDownStation(newSection);
    }

    private void addDownSection(Section newSection) {
        Section parentStation = findSections(conditionNextSection(newSection));
        parentStation.changeDownSection(newSection);
        sections.add(newSection);
    }

    private Predicate<Section> conditionNextSection(Section newSection) {
        return section -> section.equalsLastStation(newSection);
    }

    private Section findSections(Predicate<Section> condition) {
        Section findSection = sections.stream()
                                      .filter(condition)
                                      .findFirst()
                                      .orElseThrow(SectionNotFoundException::new);
        return findSection;
    }

    public boolean contains(List<Section> section) {
        return section.contains(section);
    }

    public List<Station> getStations() {
        Section findSection = findFirstSection();

        List<Station> resultStations = new ArrayList();

        while (matchStation(isDownStation(findSection))) {
            final Section section = findSection;
            resultStations.add(findSection.getUpStation());
            findSection = findSections(section::equalsDownStation);
        }

        resultStations.add(findSection.getUpStation());
        return Collections.unmodifiableList(resultStations);
    }

    public Section findFirstSection() {
        return findSections(SectionType::equalsFirst);
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
