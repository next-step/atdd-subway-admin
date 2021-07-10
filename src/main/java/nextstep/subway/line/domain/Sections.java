package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section, Line line) {
        if (sections.isEmpty()) {
            section.add(line);
            this.sections.add(section);
            return;
        }

        addNewSectionToOldSection(section, line);
    }

    public void addNewSectionToOldSection(Section newSection, Line line) {
        Station newUpStation = newSection.getUpStation();
        Station newDownStation = newSection.getDownStation();
        SectionDistance newDistance = newSection.getDistance();

        addSectionIfBetweenAndUpStationSame(newSection, newDownStation, newDistance, line);
        addSectionIfBetweenAndDownStationSame(newSection, newUpStation, newDownStation, newDistance, line);
        addSectionIfAtTheEnd(newSection, newUpStation, newDownStation, newDistance, line);
    }

    private void addSectionIfBetweenAndUpStationSame(Section newSection, Station newDownStation, SectionDistance newDistance, Line line) {
        sections.stream()
            .filter(newSection::isBetweenAndUpStationSameWith)
            .findFirst()
            .ifPresent(section -> {
                section.getDistance().checkDistanceWith(newDistance);
                sections.add(createSectionFrom(newDownStation, section.getDownStation(), section.getDistance().minus(newDistance), line));
                section.updateBy(newDownStation, newDistance);
            });
    }

    private void addSectionIfBetweenAndDownStationSame(Section newSection, Station newUpStation, Station newDownStation, SectionDistance newDistance, Line line) {
        sections.stream()
            .filter(newSection::isBetweenAndDownStationSameWith)
            .findFirst()
            .ifPresent(section -> {
                section.getDistance().checkDistanceWith(newDistance);
                sections.add(createSectionFrom(newUpStation, newDownStation, newDistance, line));
                section.updateBy(newUpStation, section.getDistance().minus(newDistance));
            });
    }

    private void addSectionIfAtTheEnd(Section newSection, Station newUpStation, Station newDownStation, SectionDistance newDistance, Line line) {
        sections.stream()
            .filter(newSection::isAtTheEndWith)
            .findFirst()
            .ifPresent(section -> sections.add(createSectionFrom(newUpStation, newDownStation, newDistance, line)));
    }

    private Section createSectionFrom(Station upStation, Station downStation, SectionDistance distance, Line line) {
        return new Section(upStation, downStation, distance, line);
    }

    public List<Section> getSections() {
        return sections;
    }

    public Stations getAllStations() {
        return new Stations(this);
    }

    public void removeSectionBy(Station station) {
        if (isSizeOne()) {
            throw new RuntimeException("Can't' remove if section size is 1");
        }

        remove(station);
    }

    private void remove(Station station) {
        removeIfLeftMost(station);
        removeIfRightMost(station);
        removeIfInMiddle(station);
    }

    private void removeIfInMiddle(Station station) {
        sections
            .stream()
            .filter(section -> section.getDownStation().equals(station) && getNextSectionOf(section).getUpStation().equals(station))
            .findFirst()
            .ifPresent(section -> {
                Section nextSection = getNextSectionOf(section);
                sections.remove(nextSection);
                section.updateBy(nextSection.getDownStation(), section.getDistance().plus(nextSection.getDistance()));
            });
    }

    private void removeIfRightMost(Station station) {
        sections
            .stream()
            .skip(sections.size() - 1)
            .filter(section -> section.getDownStation().equals(station))
            .findFirst()
            .ifPresent(sections::remove);
    }

    private void removeIfLeftMost(Station station) {
        sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findFirst()
            .ifPresent(sections::remove);
    }

    private Section getNextSectionOf(Section section) {
        return sections.get(sections.indexOf(section) + 1);
    }

    private boolean isSizeOne() {
        return sections.size() == 1;
    }
}
