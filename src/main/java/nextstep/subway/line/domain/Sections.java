package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.SectionSortingException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    private static final String ERROR_NOT_FOUND_STATIONS = "구간에서 상/하행역 정보를 찾을 수 없습니다.";
    private static final String ERROR_ALREADY_ADDED_SECTION = "상/하행역 정보가 이미 등록되어 있습니다.";
    private static final String ERROR_CANNOT_DELETE = "더 이상 삭제할 수 없습니다.";
    private static final String ERROR_NOT_CONTAINS_STATION = "지하철역이 노선에 포함되어 있지 않습니다.";
    private static final int SECTIONS_MIN_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        if (!sections.isEmpty()) {
            updateSection(section);
        }
        sections.add(section);
    }

    public void delete(Station station) {
        checkDeleteStation(station);

        findSameUpStation(station).ifPresent(downSection -> {
            findSameDownStation(downSection.getUpStation())
                .ifPresent(upSection -> upSection.mergeDownStation(downSection));
            sections.remove(downSection);
            return;
        });

        findSameDownStation(station).ifPresent(upSection -> {
            findSameUpStation(upSection.getDownStation())
                .ifPresent(downSection -> downSection.mergeUpStation(upSection));
            sections.remove(upSection);
            return;
        });
    }

    private void checkDeleteStation(Station station) {
        if (isLastSection()) {
            throw new IllegalArgumentException(ERROR_CANNOT_DELETE);
        }

        if (isNotFoundInSection(station)) {
            throw new IllegalArgumentException(ERROR_NOT_CONTAINS_STATION);
        }
    }

    private boolean isLastSection() {
        return sections.size() <= SECTIONS_MIN_SIZE;
    }

    private boolean isNotFoundInSection(Station station) {
        return !getStations().contains(station);
    }

    private void updateSection(Section section) {
        checkStationOfSection(section);

        findSameUpStation(section.getUpStation()).ifPresent(it -> {
            it.updateUpStation(section);
            return;
        });

        findSameDownStation(section.getDownStation()).ifPresent(it -> {
            it.updateDownStation(section);
            return;
        });
    }

    private Optional<Section> findSameDownStation(Station station) {
        return sections.stream()
            .filter(it -> it.isEqualToDownStation(station))
            .findAny();
    }

    private Optional<Section> findSameUpStation(Station station) {
        return sections.stream()
            .filter(it -> it.isEqualToUpStation(station))
            .findAny();
    }

    private void checkStationOfSection(Section section) {
        if (isNotContainsStation(section)) {
            throw new IllegalArgumentException(ERROR_NOT_FOUND_STATIONS);
        }
        if (isAlreadyAdded(section)) {
            throw new IllegalArgumentException(ERROR_ALREADY_ADDED_SECTION);
        }
    }

    private boolean isAlreadyAdded(Section section) {
        Set<Station> stations = getStations();
        return stations.contains(section.getUpStation())
            && stations.contains(section.getDownStation());
    }

    private boolean isNotContainsStation(Section section) {
        Set<Station> stations = getStations();
        return !stations.contains(section.getUpStation())
            && !stations.contains(section.getDownStation());
    }

    private Set<Station> getStations() {
        Set<Station> stations = new HashSet<>();
        sections.forEach(it -> {
            stations.add(it.getUpStation());
            stations.add(it.getDownStation());
        });
        return stations;
    }

    public List<Station> getSortedStations() {
        if (isStationsEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(findAllStations());
    }

    private boolean isStationsEmpty() {
        return sections.size() <= 0;
    }

    private List<Station> findAllStations() {
        Section firstSection = findFirstSection();
        List<Station> stations = new ArrayList<>(
            Arrays.asList(firstSection.getUpStation(), firstSection.getDownStation())
        );

        List<Station> upStations = upStationsOfSections();
        Section lastSection = firstSection;
        while (upStations.contains(lastSection.getDownStation())) {
            Station finalDownStation = lastSection.getDownStation();
            lastSection = sections.stream()
                .filter(section -> section.isEqualToUpStation(finalDownStation))
                .findFirst()
                .orElseThrow(SectionSortingException::new);
            stations.add(lastSection.getDownStation());
        }
        return stations;
    }

    private Section findFirstSection() {
        List<Station> downStations = downStationsOfSections();
        Section firstSection = sections.get(0);
        while (downStations.contains(firstSection.getUpStation())) {
            Station finalUpStation = firstSection.getUpStation();
            firstSection = sections.stream()
                .filter(section -> section.isEqualToDownStation(finalUpStation))
                .findFirst()
                .orElseThrow(SectionSortingException::new);
        }
        return firstSection;
    }

    private List<Station> upStationsOfSections() {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
    }

    private List<Station> downStationsOfSections() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }
}
