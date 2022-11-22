package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

        Section removedUpSection = removeUpSection(station).orElse(null);
        Section removedDownSection = removeDownSection(station).orElse(null);

        addMergeSection(removedUpSection, removedDownSection);
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

    private void addMergeSection(Section upSection, Section downSection) {
        if (Objects.isNull(upSection)) {
            return;
        }
        if (Objects.isNull(downSection)) {
            return;
        }
        addSection(upSection.merge(downSection));
    }

    public List<Section> getList() {
        return Collections.unmodifiableList(this.sections);
    }
}
