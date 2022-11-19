package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Stations allStations() {
        return sections.stream()
            .map(Section::getStations)
            .reduce(Stations::concatDistinct)
            .orElseGet(Stations::empty);
    }

    public void addSection(Section newSection) {
        Stations stations = this.allStations();
        SectionsValidator.validateAlreadyContainsAll(stations, newSection);
        SectionsValidator.validateNotContainsAny(stations, newSection);
        this.sections.forEach(section -> section.modify(newSection));
        this.sections.add(newSection);
    }

    public void removeSectionByStation(Station station) {
        SectionsValidator.validateNotContainsStation(this.allStations(), station);
        SectionsValidator.validateOnlySection(this.sections);

        Optional<Section> removedUpSection = removeUpSection(station);
        Optional<Section> removedDownSection = removeDownSection(station);

        if (removedUpSection.isPresent() && removedDownSection.isPresent()) {
            this.addSection(mergeSection(removedUpSection.get(), removedDownSection.get()));
        }
    }

    private Optional<Section> removeUpSection(Station station) {
        Optional<Section> upSection = this.sections.stream()
            .filter(section -> section.hasDownStation(station))
            .findAny();
        upSection.ifPresent(section -> this.sections.remove(section));
        return upSection;
    }

    private Optional<Section> removeDownSection(Station station) {
        Optional<Section> downSection = this.sections.stream()
            .filter(section -> section.hasUpStation(station))
            .findAny();
        downSection.ifPresent(section -> this.sections.remove(section));
        return downSection;
    }

    private Section mergeSection(Section upSection, Section downSection) {
        return upSection.merge(downSection);
    }

    public List<Section> getList() {
        return Collections.unmodifiableList(this.sections);
    }
}
