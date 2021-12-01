package nextstep.subway.line.domain;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static nextstep.subway.common.Message.*;
import static nextstep.subway.line.exception.AlreadyRegisteredException.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.AlreadyRegisteredException;
import nextstep.subway.line.exception.SectionNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotFoundException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = LAZY, cascade = ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(List<Section> newSections, Line line) {
        newSections.forEach(section -> section.setLine(line));
        this.sections.addAll(newSections);
    }

    public void add(Section newSection, Line line) {
        if (sections.contains(newSection)) {
            return;
        }
        newSection.setLine(line);
        sections.add(newSection);
    }

    public void addSection(Section newSection) {
        isNotExistsStation(newSection);
        alreadyRegisteredSection(newSection);
        changeUpSection(newSection);
        changeDownSection(newSection);
        sections.add(newSection);
    }

    private void isNotExistsStation(Section newSection) {
        if (!matchStation(section -> section.contations(newSection))) {
            throw new StationNotFoundException(MESSAGE_IS_NOT_EXISTS_STATION);
        }
    }

    void alreadyRegisteredSection(Section newSection) {
        if (matchStation(section -> section.isStations(newSection))) {
            throw new AlreadyRegisteredException(MESSAGE_ALREADY_REGISTERED_SECTION);
        }
    }

    private boolean matchStation(Predicate<Section> isStation) {
        return sections.stream()
                       .anyMatch(isStation);
    }

    private void changeUpSection(Section newSection) {
        Station upStation = newSection.getUpStation();
        if (matchStation(isUpStation(upStation))) {
            findSections(isUpStation(upStation))
                .ifPresent(section -> section.changeUpSection(newSection));
        }
    }

    private Predicate<Section> isUpStation(Station upStation) {
        return section -> section.isUpStation(upStation);
    }

    private void changeDownSection(Section newSection) {
        Station downStation = newSection.getDownStation();
        if (matchStation(isDownStation(downStation))) {
            findSections(isDownStation(downStation))
                .ifPresent(section -> section.changeDownSection(newSection));
        }
    }

    private Predicate<Section> isDownStation(Station downStation) {
        return section -> section.isDowStation(downStation);
    }

    private Optional<Section> findSections(Predicate<Section> condition) {
        return sections.stream()
                       .filter(condition)
                       .findFirst();
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public List<Station> getStations() {
        Section findSection = findFirstSection();
        List<Station> resultStations = new ArrayList<>();

        while (matchStation(isUpStation(findSection.getDownStation()))) {
            resultStations.add(findSection.getUpStation());
            findSection = findSections(isUpStation(findSection.getDownStation()))
                .orElseThrow(SectionNotFoundException::new);
        }

        resultStations.add(findSection.getUpStation());
        resultStations.add(findSection.getDownStation());
        return Collections.unmodifiableList(resultStations);
    }

    private Section findFirstSection() {
        Section section = sections.get(0);
        while (matchStation(isPreStation(section))) {
            section = findSections(isPreStation(section))
                .orElseThrow(SectionNotFoundException::new);
        }
        return section;
    }

    private Predicate<Section> isPreStation(Section section) {
        return s -> s.equalsUpStationName(section.getUpStation());
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
