package nextstep.subway.section.domain;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    private static final int LAST_STATION_COUNT = 1;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void connect(Section section) {
        Section upBoundSection = upBoundSection(section);
        Section downBoundSection = downBoundSection(section);

        validateForConnect(upBoundSection, downBoundSection, section);
        updateWhenConnectInMiddleOfSection(upBoundSection, downBoundSection, section);

        this.sections.add(section);
    }

    public void remove(Station station) {
        Section upBoundSection = upBoundSection(station);
        Section downBoundSection = downBoundSection(station);

        validateForRemove(station);

        if (isStationInMiddleOfSection(upBoundSection, downBoundSection)) {
            removeStationInMiddleOfSection(upBoundSection, downBoundSection);
            return;
        }

        removeLastSection(upBoundSection, downBoundSection);
    }

    private void removeLastSection(Section upBoundSection, Section downBoundSection) {
        if (isDownBoundSection(upBoundSection, downBoundSection)) {
            remove(upBoundSection); // 하행 종점을 삭제하는 경우
        }

        if (isUpBoundSection(upBoundSection, downBoundSection)) {
            remove(downBoundSection); // 상행 종점을 삭제하는 경우
        }
    }

    private void validateForConnect(Section upBoundSection, Section downBoundSection,
        Section section) {
        Set<Station> stations = getAllStations();

        validateAlreadyExistedStations(section, stations);
        validateDistanceWhenConnect(upBoundSection, downBoundSection, section);
        validateExistUpStationOrDownStation(section, stations);
    }

    private void validateForRemove(Station station) {
        validateExistStation(station);
        validateLastSection();
    }

    private void updateWhenConnectInMiddleOfSection(Section upBoundSection,
        Section downBoundSection,
        Section section) {
        Set<Station> stations = getAllStations();
        if (connectableByUpStation(upBoundSection, stations.contains(section.getUpStation()))) {
            upBoundSection.connectByUpStation(section);
            return;
        }

        if (connectableByDownStation(downBoundSection, stations.contains(section.getDownStation()))) {
            downBoundSection.connectByDownStation(section);
        }
    }

    private boolean connectableByUpStation(Section upBoundSection, boolean contains) {
        return !upBoundSection.isDummy() && contains;
    }

    private boolean connectableByDownStation(Section downBoundSection, boolean contains) {
        return !downBoundSection.isDummy() && contains;
    }

    private void removeStationInMiddleOfSection(Section upBoundSection, Section downBoundSection) {
        upBoundSection.updateForDelete(downBoundSection);
        remove(downBoundSection);
    }

    public static Sections empty() {
        return new Sections(new ArrayList<>());
    }

    public List<Station> extractStationsWithOrdering() {
        List<Station> stations = new ArrayList<>();

        final Map<Long, Section> sectionByUpStationId = toMapForSectionByUpStationId();
        Section section = upBoundLastSection();
        while (section != null) {
            stations.add(section.getUpStation());
            section = sectionByUpStationId.get(section.getDownStation().getId());
        }

        stations.add(downBoundLastStation());
        return stations;
    }

    private Map<Long, Section> toMapForSectionByUpStationId() {
        return this.sections.stream()
            .collect(toMap(section -> section.getUpStation().getId(), Function.identity()));
    }

    private Section upBoundLastSection() {
        final Station upBoundLastStation = upBoundLastStation();
        return this.sections.stream()
            .filter(section -> section.isEqualToUpStation(upBoundLastStation))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("상행역이 속하는 구간은 한 개가 있어야 합니다."));
    }

    private Section upBoundSection(Section standardSection) {
        return this.sections.stream()
            .filter(section -> section.isEqualToUpStation(standardSection.getUpStation()))
            .findFirst()
            .orElse(Section.DUMMY_SECTION);
    }

    private Section downBoundSection(Section standardSection) {
        return this.sections.stream()
            .filter(section -> section.isEqualToDownStation(standardSection.getDownStation()))
            .findFirst()
            .orElse(Section.DUMMY_SECTION);
    }

    private Section upBoundSection(Station station) {
        return this.sections.stream()
            .filter(section -> section.isEqualToDownStation(station))
            .findFirst()
            .orElse(Section.DUMMY_SECTION);
    }

    private Section downBoundSection(Station station) {
        return this.sections.stream()
            .filter(section -> section.isEqualToUpStation(station))
            .findFirst()
            .orElse(Section.DUMMY_SECTION);
    }

    private Station upBoundLastStation() {
        Set<Station> upStations = getUpStations();
        upStations.removeAll(getDownStations());

        if (upStations.size() != LAST_STATION_COUNT) {
            throw new IllegalStateException("상행 종착역은 한 개가 있어야 합니다.");
        }

        return upStations.stream().findAny().get();
    }

    private Station downBoundLastStation() {
        Set<Station> downStations = getDownStations();
        downStations.removeAll(getUpStations());

        if (downStations.size() != LAST_STATION_COUNT) {
            throw new IllegalStateException("하행 종착역은 한 개가 있어야 합니다.");
        }

        return downStations.stream().findAny().get();
    }

    private Set<Station> getUpStations() {
        return this.sections.stream()
            .map(Section::getUpStation)
            .collect(toSet());
    }

    private Set<Station> getDownStations() {
        return this.sections.stream()
            .map(Section::getDownStation)
            .collect(toSet());
    }

    private Set<Station> getAllStations() {
        Set<Station> stations = new HashSet<>();
        stations.addAll(getUpStations());
        stations.addAll(getDownStations());
        return stations;
    }

    private void validateDistanceWhenConnect(Section upBoundSection, Section downBoundSection,
        Section section) {

        if (!upBoundSection.isDummy()) {
            validateDistanceWhenConnect(upBoundSection, section);
            return;
        }

        if (!downBoundSection.isDummy()) {
            validateDistanceWhenConnect(downBoundSection, section);
        }
    }

    private void validateDistanceWhenConnect(Section existedSection,
        Section section) {
        if (existedSection.isLessThanOrEqualDistance(section)) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 합니다");
        }
    }

    private void validateAlreadyExistedStations(Section section, Set<Station> stations) {
        if (isExistedUpStationAndDownStation(section, stations)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다");
        }
    }

    private void validateExistUpStationOrDownStation(Section section, Set<Station> stations) {
        if (isNotExistUpStationOrDownStation(section, stations)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.");
        }
    }

    private void validateExistStation(Station station) {
        if (!getAllStations().contains(station)) {
            throw new IllegalArgumentException("노선에 존재하지 않는 역입니다.");
        }
    }

    private void validateLastSection() {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("마지막 구간은 삭제할 수 없습니다.");
        }
    }

    private boolean isNotExistUpStationOrDownStation(Section section, Set<Station> stations) {
        return !(stations.contains(section.getUpStation())
            || stations.contains(section.getDownStation()));
    }

    private boolean isExistedUpStationAndDownStation(Section section, Set<Station> stations) {
        return stations.contains(section.getUpStation())
            && stations.contains(section.getDownStation());
    }

    private void remove(Section section) {
        this.sections.remove(section);
    }

    private boolean isStationInMiddleOfSection(Section upBoundSection, Section downBoundSection) {
        return !upBoundSection.isDummy() && !downBoundSection.isDummy();
    }

    private boolean isDownBoundSection(Section upBoundSection, Section downBoundSection) {
        return !upBoundSection.isDummy() && downBoundSection.isDummy();
    }

    private boolean isUpBoundSection(Section upBoundSection, Section downBoundSection) {
        return !downBoundSection.isDummy() && upBoundSection.isDummy();
    }

    List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
