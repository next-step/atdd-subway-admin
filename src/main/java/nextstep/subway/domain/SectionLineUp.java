package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 노선 구간 목록을 관리하는 일급 컬렉션
 */
@Embeddable
public class SectionLineUp {

    private static final int START_INDEX = 0;
    private static final int ONLY_ONE = 1;

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sectionList = new ArrayList<>();

    protected SectionLineUp() {
    }

    public void addSection(Section section) {
        if (sectionList.isEmpty()) {
            sectionList.add(section);
            return;
        }
        validUnknownSection(section);
        validSameSection(section);
        add(section);
    }

    private void add(Section section) {
        if (!isEndUpStation(section) && !isEndDownStation(section)) {
            createInternalSection(section);
            return;
        }
        sectionList.add(section);
    }

    public List<Section> getSectionsInOrder() {
        Section firstSection = findFirstSection(sectionList.get(START_INDEX));
        return searchSectionsByOrder(new ArrayList<>(), firstSection);
    }

    public Stations getStationsInOrder() {

        Section firstSection = findFirstSection(sectionList.get(START_INDEX));
        final Set<Station> stations = searchStationsByOrder(new HashSet<>(), firstSection);
        return new Stations(stations.stream().distinct().collect(Collectors.toList()));
    }

    public List<Section> deleteSection(Station station) {
        validOnlyOneSection();
        if (isInternalStation(station)) {
            return deleteInternalStation(station);
        }
        return delete(station);
    }

    private void validOnlyOneSection() {
        if (sectionList.size() == ONLY_ONE) {
            throw new IllegalStateException("노선에 포함된 구간이 하나이기에 구간을 삭제할 수 없습니다");
        }
    }

    private List<Section> deleteInternalStation(Station station) {
        Section upSection = findSameDownStation(station);
        Section downSection = findSameUpStation(station);
        sectionList.add(Section.mergeByDelete(upSection, downSection));
        List<Section> deletedSections = Arrays.asList(upSection, downSection);
        sectionList.removeAll(deletedSections);
        return deletedSections;
    }

    private Section findSameDownStation(Station station) {
        return sectionList.stream().filter(section -> section.isSameDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("하행역이 같은 구간을 찾을 수 없습니다"));
    }

    private Section findSameUpStation(Station station) {
        return sectionList.stream().filter(section -> section.isSameUpStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상행역이 같은 구간을 찾을 수 없습니다"));
    }

    private List<Section> delete(Station station) {
        Section deletedSection = sectionList.stream().filter(section -> section.isKnownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("노선에 포함되지 않은 역입니다. 요청id:" + station.getId()));
        sectionList.remove(deletedSection);
        return Collections.singletonList(deletedSection);
    }

    private boolean isInternalStation(Station station) {
        return hasSameUpStation(station) && hasSameDownStation(station);
    }

    private boolean hasSameDownStation(Station station) {
        return sectionList.stream().anyMatch(section -> section.isSameDownStation(station));
    }

    private boolean hasSameUpStation(Station station) {
        return sectionList.stream().anyMatch(section -> section.isSameUpStation(station));
    }

    private boolean isEndUpStation(Section section) {
        //  도착지에서 출발하는 노선은 있지만, 도착지로 향하는 노선이 없는 경우
        return notHasSameDownStation(section) && hasSameUpStationByDownStation(section);
    }

    private boolean isEndDownStation(Section section) {
        //  출발지로 향하는 노선이 있지만, 출발지에서 출발하는 노선이 없는 경우
        return hasSameDownStationByUpStation(section) && notHasSameUpStation(section);
    }

    private void createInternalSection(Section section) {
        findInternalMatchSection(section)
                .ifPresent(matchSection -> {
                    matchSection.updateBy(section);
                    sectionList.add(section);
                });
    }

    private Optional<Section> findInternalMatchSection(Section section) {
        return sectionList.stream()
                .filter(streamSection -> streamSection.hasSameUpStation(section) ||
                        streamSection.hasSameDownStation(section))
                .findFirst();
    }

    private boolean notHasSameDownStation(Section section) {
        return sectionList.stream().noneMatch(streamSection -> streamSection.hasSameDownStation(section));
    }

    private boolean hasSameUpStationByDownStation(Section section) {
        return sectionList.stream().anyMatch(streamSection -> streamSection.sameUpStationByDownStation(section));
    }

    private boolean notHasSameUpStation(Section section) {
        return sectionList.stream().noneMatch(streamSection -> streamSection.hasSameUpStation(section));
    }

    private boolean hasSameDownStationByUpStation(Section section) {
        return sectionList.stream().anyMatch(streamSection -> streamSection.sameDownStationByUpStation(section));
    }

    private void validUnknownSection(Section section) {
        if (notHasSection(section)) {
            throw new IllegalArgumentException(
                    "상행역, 하행역이 노선에 포함되어 있지 않습니다. 상행역ID:" + section.getUpStationId() + ", 하행역ID:"
                            + section.getDownStationId());
        }
    }

    private void validSameSection(Section section) {
        if (hasKnownUpStation(section) && hasKnownDownStation(section)) {
            throw new IllegalArgumentException(
                    "이미 중복된 구간이 있습니다. 상행선id:" + section.getUpStationId() + ", 하행선id:" + section.getDownStationId());
        }
    }

    private boolean notHasSection(Section section) {
        return sectionList.stream().noneMatch(streamSection -> streamSection.isKnownSection(section));
    }

    private boolean hasKnownUpStation(Section section) {
        return sectionList.stream()
                .anyMatch(streamSection -> streamSection.isKnownStation(section.getUpStation()));
    }

    private boolean hasKnownDownStation(Section section) {
        return sectionList.stream()
                .anyMatch(streamSection -> streamSection.isKnownStation(section.getDownStation()));
    }

    private Section findFirstSection(Section section) {
        return sectionList.stream().filter(streamSection -> streamSection.hasSameDownStation(section.getUpStation()))
                .findFirst()
                .map(this::findFirstSection)
                .orElse(section);
    }

    private List<Section> searchSectionsByOrder(List<Section> sections, Section section) {
        sections.add(section);
        return sectionList.stream().filter(streamSection -> streamSection.hasSameUpStation(section.getDownStation()))
                .findFirst()
                .map(streamSection -> searchSectionsByOrder(sections, streamSection))
                .orElse(sections);
    }

    private Set<Station> searchStationsByOrder(Set<Station> stations, Section section) {
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());
        return sectionList.stream().filter(streamSection -> streamSection.hasSameUpStation(section.getDownStation()))
                .findFirst()
                .map(streamSection -> searchStationsByOrder(stations, streamSection))
                .orElse(stations);
    }
}
