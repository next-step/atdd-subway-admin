package nextstep.subway.domain;

import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidSectionNumberException;
import nextstep.subway.exception.InvalidSizeSectionException;
import nextstep.subway.exception.NoConnectedSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    private static final String NO_CONNECTED_SECTION_MESSAGE = "연결되는 구간이 아닙니다.";
    private static final String DUPLICATE_SECTION_EXCEPTION = "이미 노선에 등록되어 있는 구간입니다.";
    private static final String INVALID_SIZE_SECTION_EXCEPTION = "기존 구간에 등록할 수 없는 길이입니다.";
    private static final String INVALID_SECTION_NUMBER_EXCEPTION = "구간이 하나일 때는 삭제할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Station> getStationList() {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    public void add(Line line, Station upStation, Station downStation, int distance) {
        if (!sections.isEmpty()) {
            validate(upStation, downStation, distance);
            arrangeStations(upStation, downStation, distance);
        }
        this.sections.add(new Section(line, upStation, downStation, distance));
    }

    private void arrangeStations(Station upStation, Station downStation, int distance) {
        boolean isUpStationExisted = isUpStationExisted(upStation);
        boolean isDownStationExisted = isDownStationExisted(upStation);

        if (isUpStationExisted) {
            updateUpStation(upStation, downStation, distance);
        }
        if (isDownStationExisted) {
            updateDownStation(upStation, downStation, distance);
        }
    }

    private boolean isUpStationExisted(Station upStation) {
        return sections.stream()
                .anyMatch(it -> it.getUpStation() == upStation);
    }

    private boolean isDownStationExisted(Station downStation) {
        return sections.stream()
                .anyMatch(it -> it.getDownStation() == downStation);
    }

    private void updateUpStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(it -> it.getUpStation() == upStation)
                .findFirst()
                .ifPresent(it -> it.updateUpStation(downStation, distance));
    }

    private void updateDownStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(it -> it.getUpStation() == downStation)
                .findFirst()
                .ifPresent(it -> it.downStationUpdate(upStation, distance));
    }

    private void validate(Station upStation, Station downStation, int distance) {
        validateConnectedSection(upStation, downStation);
        validateDistinctSection(upStation, downStation);
        validateSectionSize(upStation, downStation, distance);
    }

    private void validateSectionSize(Station upStation, Station downStation, int targetDistance) {
        Section sectionByUpStation = findSectionByStation(upStation);
        Section sectionByDownStation = findSectionByStation(downStation);

        boolean upStationResult = isValidSize(targetDistance, sectionByUpStation);
        boolean downStationResult = isValidSize(targetDistance, sectionByDownStation);

        if (!upStationResult || !downStationResult) {
            throw new InvalidSizeSectionException(INVALID_SIZE_SECTION_EXCEPTION);
        }
    }

    private boolean isValidSize(int target, Section section) {
        boolean result = true;
        if (section != null) {
            int actual = section.getDistance();
            result = compareDistance(target, actual);
        }

        return result;
    }

    private boolean compareDistance(int target, int actual) {
        return actual > target;
    }

    private void validateDistinctSection(Station upStation, Station downStation) {
        Section sectionByUpStation = findSectionByStation(upStation);
        Section sectionByDownStation = findSectionByStation(downStation);

        if (sectionByUpStation != null && sectionByDownStation != null) {
            throw new DuplicateSectionException(DUPLICATE_SECTION_EXCEPTION);
        }
    }

    private void validateConnectedSection(Station upStation, Station downStation) {
        Section sectionByUpStation = findSectionByStation(upStation);
        Section sectionByDownStation = findSectionByStation(downStation);

        if (sectionByUpStation == null && sectionByDownStation == null) {
            throw new NoConnectedSectionException(NO_CONNECTED_SECTION_MESSAGE);
        }
    }

    private Section findSectionByStation(Station station) {
        return sections.stream()
                .filter(it -> it.hasSameNameStation(station))
                .findFirst()
                .orElse(null);
    }

    public void deleteStation(Long stationId) {
        if (sections.size() <= 1) {
            throw new InvalidSectionNumberException(INVALID_SECTION_NUMBER_EXCEPTION);
        }

        Optional<Section> upStation = findUpStation(stationId);
        Optional<Section> downStation = findDownStation(stationId);

        upStation.ifPresent(it -> sections.remove(it));
        downStation.ifPresent(it -> sections.remove(it));
    }

    private Optional<Section> findDownStation(Long stationId) {
        return sections.stream()
                .filter(it -> it.equalsDownStation(stationId))
                .findFirst();
    }

    private Optional<Section> findUpStation(Long stationId) {
        return sections.stream()
                .filter(it -> it.equalsUpStation(stationId))
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
