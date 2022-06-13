package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    private static final int DUPLICATION_CHECK_NUMBER = 1;
    private static final int MIN_SECTION_NUMBER = 1;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "line",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (!sections.isEmpty()) {
            validateDuplicateSection(section);
            Section connectionSection = findConnectableSection(section);
            connectionSection.addMerge(section);
        }
        sections.add(section);
    }

    private void validateDuplicateSection(Section section) {
        if (getSectionStream(section).count() > DUPLICATION_CHECK_NUMBER) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");
        }
    }

    private Section findConnectableSection(Section section) {
        return getSectionStream(section)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("연결할 수 있는 구간이 없습니다."));
    }

    private Stream<Section> getSectionStream(Section section) {
        return sections.stream()
                .filter(s -> s.isSameAnyStation(section.getUpStation()) || s.isSameAnyStation(section.getDownStation()));
    }

    public List<Station> getStationsInOrder() {
        Station station = findFirstUpStation();

        List<Station> stations = new ArrayList<>();

        Map<Station, Station> map = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
        while (map.get(station) != null) {
            stations.add(station);
            station = map.get(station);
        }
        stations.add(station);
        return stations;
    }

    private Station findFirstUpStation() {
        Set<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());
        Set<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
        return upStations.stream()
                .filter(upStation -> !downStations.contains(upStation))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public void delete(Station station) {
        if (sections.size() <= MIN_SECTION_NUMBER) {
            throw new IllegalArgumentException("더 이상 구간을 제거할 수 없습니다.");
        }

        Optional<Section> sectionIncludedUpStation = getSectionByFilter(section -> section.isSameUpStation(station));
        Optional<Section> sectionIncludedDownStation = getSectionByFilter(section -> section.isSameDownStation(station));
        removeAndMerge(sectionIncludedUpStation, sectionIncludedDownStation);
    }

    private void removeAndMerge(Optional<Section> sectionIncludedUpStation,
                                Optional<Section> sectionIncludedDownStation) {
        if (!sectionIncludedUpStation.isPresent() && !sectionIncludedDownStation.isPresent()) {
            throw new IllegalArgumentException("해당 노선에 존재하지 않는 역입니다.");
        }

        if (sectionIncludedUpStation.isPresent() && sectionIncludedDownStation.isPresent()) {
            removeMiddleStation(sectionIncludedUpStation.get(), sectionIncludedDownStation.get());
            return;
        }

        removeLastStation(sectionIncludedUpStation, sectionIncludedDownStation);
    }

    private void removeMiddleStation(Section sectionIncludedUpStation, Section sectionIncludedDownStation) {
        sectionIncludedDownStation.deleteMerge(sectionIncludedUpStation);
        sections.remove(sectionIncludedUpStation);
    }

    private void removeLastStation(Optional<Section> sectionIncludedUpStation,
                                   Optional<Section> sectionIncludedDownStation) {
        sectionIncludedUpStation.ifPresent(section -> sections.remove(section));
        sectionIncludedDownStation.ifPresent(section -> sections.remove(section));
    }

    private Optional<Section> getSectionByFilter(Predicate<Section> filter) {
        return sections.stream()
                .filter(filter)
                .findFirst();
    }

    public List<Section> getSections() {
        return sections;
    }
}