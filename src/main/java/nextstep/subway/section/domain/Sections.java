package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.global.exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.global.exception.CannotRegisterException;
import nextstep.subway.global.exception.ExceptionType;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> items = new ArrayList<>();

    public void add(Section newSection) {
        if (items.isEmpty()) {
            items.add(newSection);
            return;
        }

        validateSections(newSection);
        if (isEndPointSection(newSection)) {
            items.add(newSection);
            return;
        }

        registerSection(newSection);
    }

    private boolean isEndPointSection(Section newSection) {
        return this.items.stream()
            .noneMatch(item -> item.isStationConnectable(newSection));
    }

    private void registerSection(Section newSection) {
        Section connectedSection = getConnectedSection(newSection);
        Section createdSection = connectedSection.addBetweenSection(newSection);
        items.add(createdSection);
    }

    private Section getConnectedSection(Section newSection) {
        return this.items.stream()
            .filter(section -> section.isStationConnectable(newSection))
            .findAny()
            .orElseThrow(RuntimeException::new);
    }

    private void validateSections(Section section) {
        boolean upStationContains = isContainsStation(section.getUpStation());
        boolean downStationContains = isContainsStation(section.getDownStation());

        if (upStationContains && downStationContains) {
            throw new CannotRegisterException(ExceptionType.IS_EXIST_BOTH_STATIONS);
        }

        if (!upStationContains && !downStationContains) {
            throw new CannotRegisterException(ExceptionType.IS_NOT_EXIST_BOTH_STATIONS);
        }
    }

    private boolean isContainsStation(Station station) {
        return this.items.stream()
            .anyMatch(item -> item.isContains(station));
    }

    public List<Section> getItems() {
        return items;
    }

    public Section getFirstSection() {
        Station firstStation = getFirstStation();

        return items.stream()
            .filter(item -> item.isEqualsUpStation(firstStation))
            .findAny()
            .orElseThrow(() -> new NotFoundException(ExceptionType.NOT_FOUND_LINE_STATION));
    }

    private Station getFirstStation() {
        List<Station> upStations = this.items.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());

        return upStations.stream()
            .filter(station -> isNoneMatchedStation(this.items, station))
            .findAny()
            .orElseThrow(() -> new NotFoundException(ExceptionType.NOT_FOUND_LINE_STATION));
    }

    private boolean isNoneMatchedStation(List<Section> sections, Station upStation) {
        return sections.stream()
            .noneMatch(section -> section.isEqualsDownStation(upStation));
    }

    public Optional<Section> getNextSection(Section section) {
        return this.items.stream()
            .filter(item -> item.isEqualsUpStation(section.getDownStation()))
            .findAny();
    }
}
