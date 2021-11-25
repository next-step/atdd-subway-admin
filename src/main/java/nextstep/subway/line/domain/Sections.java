package nextstep.subway.line.domain;

import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = LAZY, cascade = ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(List<Section> sections, Line line) {
        List<Section> addSections = sections.stream()
                                            .peek(section -> section.setLine(line))
                                            .collect(toList());
        this.sections.addAll(addSections);
    }

    public void add(Section section, Line line) {
        section.setLine(line);
        this.sections.add(section);
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

}
