package nextstep.subway.domain.Section;

import static nextstep.subway.domain.Section.SectionsValidator.validAddSectionDistance;
import static nextstep.subway.domain.Section.SectionsValidator.validDeleteSectionStation;
import static nextstep.subway.domain.Section.SectionsValidator.validExistUpStationAndDownStation;
import static nextstep.subway.domain.Section.SectionsValidator.validNoExistUpStationAndDownStation;
import static nextstep.subway.message.ErrorMessage.SECTION_IS_NO_SEARCH;
import static nextstep.subway.message.ErrorMessage.STATION_IS_NO_SEARCH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "sectionId")
    private List<Section> sectionElement = new ArrayList<>();


    public Sections() {

    }

    public Sections(Section newSection) {
        this.sectionElement.add(newSection);
    }

    public List<Section> sortedSectionList() {
        sortedSections();
        return sectionList();
    }

    public List<Section> sectionList() {
        return Collections.unmodifiableList(sectionElement);
    }

    public int size() {
        return sectionElement.size();
    }

    private void sortedSections() {
        List<Section> list = new ArrayList<>();

        while (!sectionElement.isEmpty()) {
            Section lastUpSection = findByUpStation(getLastUpStation());
            list.add(lastUpSection);
            sectionElement.remove(lastUpSection);
        }

        sectionElement = list;
    }


    public void addSection(Section newSection) {
        validExistUpStationAndDownStation(this, newSection); // 구간 존재
        validNoExistUpStationAndDownStation(this, newSection); // 상행선 하행선 존재하지 않음

        if (isBetweenAddSection(newSection)) {
            addBetweenSection(newSection);
            return;
        }
        this.sectionElement.add(newSection);
    }

    private boolean isBetweenAddSection(Section newSection) {
        return hasDownStation(newSection.getDownStation());
    }

    private void addBetweenSection(Section newSection) {
        Section beforeSection = findByDownStation(newSection.getDownStation());

        validAddSectionDistance(newSection, beforeSection);

        beforeSection.minusDistance(newSection.getDistance());
        this.sectionElement.add(newSection);
    }

    public void deleteSectionStation(Station station) {
        validDeleteSectionStation(this, station);
        if (isBetweenRemoveStation(station)) {
            betweenRemoveStation(station);
            return;
        }
        lastStationRemove(station);
    }

    private boolean isBetweenRemoveStation(Station station) {
        return hasDownStation(station) && hasUpStation(station);
    }

    private void betweenRemoveStation(Station station) {
        final Section upSection = findByUpStation(station);
        final Section downSection = findByDownStation(station);
        this.sectionElement.remove(upSection);
        this.sectionElement.remove(downSection);

        this.sectionElement.add(Section.builder()
                .upStation(downSection.getUpStation())
                .downStation(upSection.getDownStation())
                .distance(Distance.sumDistance(upSection.getDistance(), downSection.getDistance()))
                .build());
    }

    private void lastStationRemove(Station station) {
        if (hasDownStation(station)) {
            this.sectionElement.remove(findByDownStation(station));
            return;
        }
        if (hasUpStation(station)) {
            this.sectionElement.remove(findByUpStation(station));
        }
    }

    public Station getLastUpStation() {
        return getUpStations()
                .filter(stationsIsNotContains(getDownStations()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(STATION_IS_NO_SEARCH.toMessage()));
    }

    public Station getLastDownStation() {
        return getDownStations()
                .filter(stationsIsNotContains(getUpStations()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(STATION_IS_NO_SEARCH.toMessage()));

    }

    private Section findByUpStation(Station findStation) {
        return this.sectionElement.stream()
                .filter(section -> section.getUpStation().equals(findStation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(SECTION_IS_NO_SEARCH.toMessage()));
    }

    private Section findByDownStation(Station findStation) {
        return this.sectionElement.stream()
                .filter(section -> section.getDownStation().equals(findStation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(SECTION_IS_NO_SEARCH.toMessage()));
    }


    public boolean hasDownStation(Station station) {
        return getDownStations().anyMatch((downStation) -> downStation.equals(station));
    }

    boolean hasUpStation(Station station) {
        return getUpStations().anyMatch((upStation) -> upStation.equals(station));
    }

    private Stream<Station> getDownStations() {
        return this.sectionElement
                .stream()
                .map(Section::getDownStation);
    }

    private Stream<Station> getUpStations() {
        return this.sectionElement
                .stream()
                .map(Section::getUpStation);
    }

    private Predicate<Station> stationsIsNotContains(Stream<Station> searchStations) {
        final List<Station> searchStationsList = searchStations.collect(Collectors.toList());
        return station -> !searchStationsList.contains(station);
    }

}
