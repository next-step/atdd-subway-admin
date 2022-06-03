package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.global.exception.CannotDeleteException;
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

    public void deleteStation(Station station) {
        validateSectionsSize();
        Section upStationConnectedSection = upStationConnectedSection(station);
        Section downStationConnectedSection = downStationConnectedSection(station);
        remove(upStationConnectedSection, downStationConnectedSection);
    }

    private void validateSectionsSize() {
        if (items.size() <= 1) {
            throw new CannotDeleteException(ExceptionType.CAN_NOT_DELETE_LINE_STATION);
        }
    }

    private void remove(Section upStationConnectedSection, Section downStationConnectedSection) {
        validateExistsStation(upStationConnectedSection, downStationConnectedSection);

        if (isStationInBetween(upStationConnectedSection, downStationConnectedSection)) {
            relocate(upStationConnectedSection, downStationConnectedSection);
            return;
        }

        removeEndStation(upStationConnectedSection, downStationConnectedSection);
    }

    private void relocate(Section upStationConnectedSection, Section downStationConnectedSection) {
        downStationConnectedSection.relocate(upStationConnectedSection);
        this.items.remove(upStationConnectedSection);
    }

    private void removeEndStation(Section upStationConnectedSection, Section downStationConnectedSection) {
        if (!upStationConnectedSection.isEmpty()) {
            this.items.remove(upStationConnectedSection);
        }

        if (!downStationConnectedSection.isEmpty()) {
            this.items.remove(downStationConnectedSection);
        }
    }

    private void validateExistsStation(Section upStationConnectedSection, Section downStationConnectedSection) {
        if (upStationConnectedSection.isEmpty() && downStationConnectedSection.isEmpty()) {
            throw new CannotDeleteException(ExceptionType.NOT_FOUND_LINE_STATION);
        }
    }

    private boolean isStationInBetween(Section upStationConnectedSection, Section downStationConnectedSection) {
        return !upStationConnectedSection.isEmpty() && !downStationConnectedSection.isEmpty();
    }

    public Section upStationConnectedSection(Station station) {
        return items.stream()
            .filter(section -> section.isEqualsUpStation(station))
            .findAny()
            .orElse(Section.empty());
    }

    public Section downStationConnectedSection(Station station) {
        return items.stream()
            .filter(section -> section.isEqualsDownStation(station))
            .findAny()
            .orElse(Section.empty());
    }

    private boolean isContainsStation(Station station) {
        return this.items.stream()
            .anyMatch(item -> item.isContains(station));
    }

    public List<Section> getItems() {
        return items;
    }

    public List<Station> getOrderedStations() {
        List<Station> stations = new ArrayList<>();
        Section section = this.getFirstSection();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());

        Optional<Section> optionalNextSection = this.getNextSection(section);
        while (optionalNextSection.isPresent()) {
            Section nextSection = optionalNextSection.get();
            stations.add(nextSection.getDownStation());
            optionalNextSection = this.getNextSection(nextSection);
        }

        return stations;
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
