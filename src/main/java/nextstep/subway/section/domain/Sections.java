package nextstep.subway.section.domain;

import nextstep.subway.section.exception.InvalidAddSectionException;
import nextstep.subway.section.exception.InvalidDeleteSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final Integer MIN_SECTION_SIZE = 1;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private final List<Section> sections = new ArrayList<>();

    public void addInitSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
        }
    }

    public void addSection(Section section) {
        checkContainSection(section);
        checkContainAnyStations(section);

        addSectionInMatch(section);
    }

    public void removeSection(Station station) {
        checkSectionsSize();
        checkExistStation(station);

        deleteSectionInMatch(station);
    }

    public List<StationResponse> getStationResponses() {
        return getStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    private void checkContainSection(Section section) {
        List<Station> stations = getStations();
        List<Station> stationsOfNewSection = Arrays.asList(section.getUpStation(), section.getDownStation());
        if (stations.containsAll(stationsOfNewSection)) {
            throw new InvalidAddSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    private void checkContainAnyStations(Section section) {
        List<Station> stations = getStations();
        if(!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new InvalidAddSectionException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    private void checkSectionsSize() {
        if (sections.size() == MIN_SECTION_SIZE) {
            throw new InvalidDeleteSectionException("구간이 하나여서 지울 수 없습니다.");
        }
    }

    private void checkExistStation(Station station) {
        List<Station> stations = getStations();
        if (!stations.contains(station)) {
            throw new InvalidDeleteSectionException("노선에 등록되어 있지 않은 지하철입니다.");
        }
    }

    private void addSectionInMatch(Section newSection) {
        // 기존 Section 상행 종점과 새로운 Section 상행 종점이 일치한 경우
        addSectionUpToUp(newSection);

        // 기존 Section 하행 종점과 새로운 Section 하행 종점이 일치한 경우
        addSectionDownToDown(newSection);

        sections.add(newSection);
    }

    private void addSectionUpToUp(Section newSection) {
        Optional<Section> oldSection = findSection(section -> section.equalUpUpStation(newSection));
        oldSection.ifPresent(section -> {
            checkDistance(section, newSection);
            section.updateUpStation(newSection.getDownStation(), section.minusDistance(newSection));
        });
    }

    private void addSectionDownToDown(Section newSection) {
        Optional<Section> oldSection = findSection(section -> section.equalDownDownStation(newSection));
        oldSection.ifPresent(section -> {
            checkDistance(section, newSection);
            section.updateDownStation(newSection.getUpStation(), section.minusDistance(newSection));
        });
    }

    private Optional<Section> findSection(Predicate<Section> predicate) {
        return sections.stream()
                .filter(predicate)
                .findFirst();
    }

    private void checkDistance(Section oldSection, Section newSection) {
        if (newSection.isEqualOrMoreDistance(oldSection)) {
            throw new InvalidAddSectionException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수가 없습니다.");
        }
    }

    private void deleteSectionInMatch(Station station) {
        Section preSection = findSection(section -> section.equalDownStation(station)).orElse(null);
        Section nextSection = findSection(section -> section.equalUpStation(station)).orElse(null);

        if (isNotNull(preSection)&& isNotNull(nextSection)) {
            modifySection(preSection, nextSection);
        }
        sections.remove(preSection);
        sections.remove(nextSection);
    }

    private void modifySection(Section preSection, Section nextSection) {
        sections.add(Section.builder()
                .upStation(preSection.getUpStation())
                .downStation(nextSection.getDownStation())
                .distance(preSection.getDistance() + nextSection.getDistance())
                .build());
        sections.remove(preSection);
        sections.remove(nextSection);
    }

    private List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Section firstSection = findFirstSection();
        stations.add(firstSection.getUpStation());

        Section nextSection = firstSection;
        while(isNotNull(nextSection)) {
            stations.add(nextSection.getDownStation());
            nextSection = findNextSection(nextSection.getDownStation()).orElse(null);
        }
        return stations;
    }

    private Section findFirstSection() {
        List<Station> downStations = getDownStations();

        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("첫번째 Section을 찾을 수가 없습니다."));
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private boolean isNotNull(Section section) {
        return section != null;
    }

    private Optional<Section> findNextSection(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst();
    }
}
