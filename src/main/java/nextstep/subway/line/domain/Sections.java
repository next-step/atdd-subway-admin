package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        addNewSectionToOldSection(section);
    }

    public void addNewSectionToOldSection(Section newSection) {
        Station newUpStation = newSection.getUpStation();
        Station newDownStation = newSection.getDownStation();
        SectionDistance newDistance = newSection.getDistance();

        addSectionIfBetweenAndUpStationSame(newSection, newDownStation, newDistance);
        addSectionIfBetweenAndDownStationSame(newSection, newUpStation, newDownStation, newDistance);
        addSectionIfAtTheEnd(newSection, newUpStation, newDownStation, newDistance);
    }

    private void addSectionIfBetweenAndUpStationSame(Section newSection, Station newDownStation, SectionDistance newDistance) {
        sections.stream()
            .filter(newSection::isBetweenAndUpStationSameWith)
            .findFirst()
            .ifPresent(section -> {
                section.getDistance().checkDistanceWith(newDistance);
                sections.add(createSectionFrom(newDownStation, section.getDownStation(), section.getDistance().minus(newDistance)));
                section.updateBy(newDownStation, newDistance);
            });
    }

    private void addSectionIfBetweenAndDownStationSame(Section newSection, Station newUpStation, Station newDownStation, SectionDistance newDistance) {
        sections.stream()
            .filter(newSection::isBetweenAndDownStationSameWith)
            .findFirst()
            .ifPresent(section -> {
                section.getDistance().checkDistanceWith(newDistance);
                sections.add(createSectionFrom(newUpStation, newDownStation, newDistance));
                section.updateBy(newUpStation, section.getDistance().minus(newDistance));
            });
    }

    private void addSectionIfAtTheEnd(Section newSection, Station newUpStation, Station newDownStation, SectionDistance newDistance) {
        sections.stream()
            .filter(newSection::isAtTheEndWith)
            .findFirst()
            .ifPresent(section -> sections.add(createSectionFrom(newUpStation, newDownStation, newDistance)));
    }

    private Section createSectionFrom(Station upStation, Station downStation, SectionDistance distance) {
        return new Section(upStation, downStation, distance);
    }

    public List<Section> getSections() {
        return sections;
    }

    public Stations getAllStations() {
        return new Stations(this);
    }
}
