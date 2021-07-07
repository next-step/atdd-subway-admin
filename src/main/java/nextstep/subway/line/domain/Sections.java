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
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);

            if (isLeftMostStation(station, section)) {
                sections.remove(section);
                return;
            }

            if (isRightMostStation(station, i, section)) {
                sections.remove(section);
                return;
            }

            Section nextSection = sections.get(i + 1);
            if (isMiddleStation(station, section, nextSection)) {
                sections.remove(nextSection);
                section.updateBy(nextSection.getDownStation(), section.getDistance().plus(nextSection.getDistance()));
                return;
            }
        }
    }

    private boolean isSizeOne() {
        return sections.size() == 1;
    }

    private boolean isLeftMostStation(Station station, Section section) {
        return section.getUpStation().equals(station);
    }

    private boolean isRightMostStation(Station station, int i, Section section) {
        return isLastSection(i) && section.getDownStation().equals(station);
    }

    private boolean isMiddleStation(Station station, Section section, Section nextSection) {
        return section.getDownStation().equals(station) && nextSection.getUpStation().equals(station);
    }

    private boolean isLastSection(int i) {
        return i + 1 == sections.size();
    }
}
