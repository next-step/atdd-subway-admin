package nextstep.subway.domain;

import nextstep.subway.Exception.NotFoundStationException;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final int MIN_SECTION_SIZE = 1;
    private static final String ERROR_MESSAGE_ALL_ALREADY_REGISTERED = "상행 역과 하행 역이 이미 모두 등록되어 있어 구간을 추가할 수 없습니다.";
    private static final String ERROR_MESSAGE_NOT_CONTAINS = "상행 역과 하행 역 둘 중 하나도 포함되어있지 않아 구간을 추가할 수 없습니다.";
    private static final String ERROR_MESSAGE_MIN_SECTION_SIZE = "구간이 1개 이하인 노선은 구간을 삭제할 수 없습니다.";
    private static final String ERROR_MESSAGE_NO_FIRST_SECTION = "첫 구간이 존재하지 않습니다.";
    private static final String ERROR_MESSAGE_NO_LAST_SECTION = "마지막 구간이 존재하지 않습니다.";
    private static final String ERROR_MESSAGE_NO_UP_SECTION = "해당 역을 상행 종점으로 가지는 구간이 없습니다.";
    private static final String ERROR_MESSAGE_NO_DOWN_SECTION = "해당 역을 하행 종점으로 가지는 구간이 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }
        validateAddSection(newSection);
        sections.forEach(section -> section.update(newSection));
        sections.add(newSection);
    }

    private void validateAddSection(Section newSection) {
        if (hasUpStationAndDownStation(newSection)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_ALL_ALREADY_REGISTERED);
        }
        if (hasNotUpStationAndDownStation(newSection)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_CONTAINS);
        }
    }

    private boolean hasUpStationAndDownStation(Section newSection) {
        List<Station> stations = findAllStations();
        return stations.contains(newSection.getUpStation()) && stations.contains(newSection.getDownStation());
    }

    private boolean hasNotUpStationAndDownStation(Section newSection) {
        List<Station> stations = findAllStations();
        return !stations.contains(newSection.getUpStation()) && !stations.contains(newSection.getDownStation());
    }

    public void delete(Station station) throws NotFoundStationException {
        validateDelete(station);

        Section firstSection = findFirstSection();
        Section lastSection = findLastSection();
        if (firstSection.getUpStation().equals(station)) {
            deleteFirstOrLastSection(firstSection);
            return;
        }
        if (lastSection.getDownStation().equals(station)) {
            deleteFirstOrLastSection(lastSection);
            return;
        }

        deleteMiddleSection(station);
    }

    private void validateDelete(Station station) throws NotFoundStationException {
        if (!findAllStations().contains(station)) {
            throw new NotFoundStationException(station.getId());
        }
        if (sections.size() <= MIN_SECTION_SIZE) {
            throw new IllegalArgumentException(ERROR_MESSAGE_MIN_SECTION_SIZE);
        }
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(section -> isEmptyDownStation(section.getUpStation()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_NO_FIRST_SECTION));
    }

    private boolean isEmptyDownStation(Station upStation) {
        int equalCount = 0;
        for (Station downStation : getDownStations()) {
            equalCount = matchEqaulCount(equalCount, upStation, downStation);
        }
        return equalCount == 0;
    }


    private List<Station> getDownStations() {
        return Collections.unmodifiableList(sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList()));
    }

    private Section findLastSection() {
        return sections.stream()
                .filter(section -> isEmptyUpStation(section.getDownStation()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_NO_LAST_SECTION));
    }

    private boolean isEmptyUpStation(Station downStation) {
        int equalCount = 0;
        for (Station upStation : getUpStations()) {
            equalCount = matchEqaulCount(equalCount, upStation, downStation);
        }
        return equalCount == 0;
    }

    private int matchEqaulCount(int equalCount, Station upStation, Station downStation) {
        if (Objects.equals(upStation.getId(), downStation.getId()) && Objects.equals(upStation.getName(), downStation.getName())) {
            equalCount++;
        }
        return equalCount;
    }

    private List<Station> getUpStations() {
        return Collections.unmodifiableList(sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList()));
    }


    private void deleteFirstOrLastSection(Section section) {
        sections.remove(section);
        section.setLine(null);
    }

    private void deleteMiddleSection(Station station) {
        Section downSection = findDownSection(station);
        Section upSection = findUpSection(station);
        downSection.merge(upSection);
        sections.remove(upSection);
    }

    private Section findDownSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_NO_UP_SECTION));
    }

    private Section findUpSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_NO_DOWN_SECTION));
    }

    public List<Station> findAllStations() {
        return Collections.unmodifiableList(sections.stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList()));
    }

    public List<Section> getSections() {
        return sections;
    }
}
