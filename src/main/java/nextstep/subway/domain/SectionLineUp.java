package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 노선 구간 목록을 관리하는 일급 컬렉션
 */
@Embeddable
public class SectionLineUp {

    private static final int START_INDEX = 0;

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sectionList = new ArrayList<>();

    protected SectionLineUp() {
    }

    public void addSection(Section section) {
        if (sectionList.isEmpty()) {
            sectionList.add(section);
            return;
        }
        validUnknownStation(section);
        validSameSection(section);
        add(section);
    }

    private void add(Section section) {
        if (!isEndUpStation(section) && !isEndDownStation(section)) {
            createInternal(section);
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
        final List<Station> stations = searchStationsByOrder(new ArrayList<>(), firstSection);
        return new Stations(stations.stream().distinct().collect(Collectors.toList()));
    }

    private boolean isEndUpStation(Section section) {
        //  출발지가 같은 노선이 없고, 도착지에서 출발하는 노선은 있지만, 도착지로 향하는 노선이 없는 경우
        return notHasSameUpStation(section) && notHasSameDownStation(section) && hasSameUpStationByDownStation(section);
    }

    private boolean isEndDownStation(Section section) {
        //  출발지로 향하는 노선이 있지만, 출발지에서 출발하는 노선이 없는 경우
        return hasSameDownStationByUpStation(section) && notHasSameUpStation(section);
    }

    private void createInternal(Section section) {
        if (hasSameUpStation(section)) {
            createInternalUpStation(section);
            return;
        }
        if (hasSameDownStation(section)) {
            createInternalDownStation(section);
        }
    }

    private void createInternalUpStation(Section section) {
        sectionList.stream().filter(streamSection -> streamSection.hasSameUpStation(section))
                .findFirst()
                .ifPresent(existSection -> {
                    existSection.updateDistance(existSection.minusDistance(section));
                    existSection.updateUpStation(section.getDownStation());
                    sectionList.add(section);
                });
    }

    private void createInternalDownStation(Section section) {
        sectionList.stream().filter(streamSection -> streamSection.hasSameDownStation(section))
                .findFirst()
                .ifPresent(existSection -> {
                    existSection.updateDistance(existSection.minusDistance(section));
                    existSection.updateDownStation(section.getUpStation());
                    sectionList.add(section);
                });
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

    private boolean hasSameUpStation(Section section) {
        return sectionList.stream().anyMatch(streamSection -> streamSection.hasSameUpStation(section));
    }

    private boolean hasSameDownStation(Section section) {
        return sectionList.stream().anyMatch(streamSection -> streamSection.hasSameDownStation(section));
    }

    private void validUnknownStation(Section section) {
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

    private List<Section> searchSectionsByOrder(ArrayList<Section> sections, Section section) {
        sections.add(section);
        return sectionList.stream().filter(streamSection -> streamSection.hasSameUpStation(section.getDownStation()))
                .findFirst()
                .map(streamSection -> searchSectionsByOrder(sections, streamSection))
                .orElse(sections);
    }

    private List<Station> searchStationsByOrder(List<Station> stations, Section section) {
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());
        return sectionList.stream().filter(streamSection -> streamSection.hasSameUpStation(section.getDownStation()))
                .findFirst()
                .map(streamSection -> searchStationsByOrder(stations, streamSection))
                .orElse(stations);
    }
}
