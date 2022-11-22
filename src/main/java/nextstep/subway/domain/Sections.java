package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public List<Integer> getDistances() {
        return sections.stream().map(Section::getDistance).collect(Collectors.toList());
    }

    public Set<String> getStationNames() {
        Set<String> stationNames = new LinkedHashSet<>();
        sections.forEach(section -> {
            stationNames.add(section.getUpStation().getName());
            stationNames.add(section.getDownStation().getName());
        });
        return stationNames;
    }

    public void addDefaultSection(Line line, int distance, Station upStation, Station downStation) {
        sections.add(new Section(line, distance, upStation, downStation));
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public void addSection(int distance, Station upStation, Station downStation) {
        validateAlreadyExistsStation(upStation, downStation);
        validateNotExistsStation(upStation, downStation);

        addBetweenByUpStation(upStation, downStation, distance);
    }

    public void deleteSectionByStation(Line line, Station station) {
        validateRemoveStationOfOneSection(station);

        removeLastStation(line, station);
    }

    private void validateRemoveStationOfOneSection(Station station) {
        if (sections.size() == 1 && isFindSameStation(station)) {
            throw new IllegalArgumentException("구간이 하나인 노선은 제거할 수 없습니다.");
        }
    }

    private void removeBetweenStation(Line line, Station station) {
        if (isNotFirstStationAndNotLastStationOfSections(station)) {
            Section findSameDownStation = findSameDownStation(station)
                    .orElseThrow(EntityNotFoundException::new);
            Section findSameUpStation = findSameUpStation(station)
                    .orElseThrow(EntityNotFoundException::new);

            sections.add(deleteBetweenStationAndCreateNewSection(line, findSameDownStation, findSameUpStation));
        }
    }

    private Section deleteBetweenStationAndCreateNewSection(Line line, Section findSameDownStation,
                                                            Section findSameUpStation) {
        isGreaterThanThenMinusSortNo(findSameDownStation.getSortNo());
        removeSection(findSameDownStation);
        removeSection(findSameUpStation);

        return new Section(line, findSameDownStation.getDistance() + findSameUpStation.getDistance(),
                findSameDownStation.getSortNo(), findSameDownStation.getUpStation(),
                findSameUpStation.getDownStation());
    }

    private boolean isNotFirstStationAndNotLastStationOfSections(Station station) {
        return !sections.get(sections.size() - 1).isEqualsDownStation(station) && !sections.get(0)
                .isEqualsUpStation(station);
    }

    private void removeLastStation(Line line, Station station) {
        Section lastSection = sections.get(sections.size() - 1);
        if (lastSection.isEqualsDownStation(station)) {
            removeSection(lastSection);
            isGreaterThanThenMinusSortNo(lastSection.getSortNo());
            return;
        }
        removeBetweenStation(line, station);
    }

    private boolean isFindSameStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isEqualsUpStation(station) || section.isEqualsDownStation(station));
    }

    private Optional<Section> findSameUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .findFirst();
    }

    private Optional<Section> findSameDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsDownStation(station))
                .findFirst();
    }

    private void validateAlreadyExistsStation(Station upStation, Station downStation) {
        sections.forEach(section -> section.validateAlreadyExistsStation(upStation, downStation));
    }

    private void validateNotExistsStation(Station upStation, Station downStation) {
        sections.forEach(section -> section.validateNotExistsStation(upStation, downStation));
    }

    private void isGreaterThanThenPlusSortNo(int sortNo) {
        sections.forEach(section -> section.ifGreaterThanThenPlusSortNo(sortNo));
    }

    private void isGreaterThanThenMinusSortNo(int sortNo) {
        sections.forEach(section -> section.ifGreaterThanThenMinusSortNo(sortNo));
    }

    private void isLessThanThenPlusSortNo(int sortNo) {
        sections.forEach(section -> section.ifLessThanThenPlusSortNo(sortNo));
    }

    private void addBetweenByUpStation(Station upStation, Station downStation, int distance) {
        Optional<Section> sameUpStation = findSameUpStation(upStation);
        sameUpStation.ifPresent(section -> {
            section.validateLength(distance);
            isGreaterThanThenPlusSortNo(section.getSortNo());
            sections.add(section.createNewSection(distance, section.getSortNo(), upStation, downStation));
            sections.add(section.createNewSection(section.getMinusDistance(distance), section.getSortNo() + 1,
                    downStation, section.getDownStation()));
            removeSection(section);
        });

        if (!sameUpStation.isPresent()) {
            addBetweenByDownStation(upStation, downStation, distance);
        }
    }

    private void addBetweenByDownStation(Station upStation, Station downStation, int distance) {
        Optional<Section> sameDownStation = findSameDownStation(downStation);
        sameDownStation.ifPresent(section -> {
            section.validateLength(distance);
            isGreaterThanThenPlusSortNo(section.getSortNo());
            sections.add(section.createNewSection(distance, section.getSortNo(), section.getUpStation(), upStation));
            sections.add(section.createNewSection(section.getMinusDistance(distance), section.getSortNo() + 1,
                    upStation, downStation));
            removeSection(section);
        });

        if (!sameDownStation.isPresent()) {
            prependUpStation(upStation, downStation, distance);
        }
    }

    private void prependUpStation(Station upStation, Station downStation, int distance) {
        Optional<Section> sameUpStation = findSameUpStation(downStation);
        sameUpStation.ifPresent(section -> {
            isLessThanThenPlusSortNo(section.getSortNo());
            sections.add(section.createNewPrependUpStation(distance, section.getSortNo() - 1, upStation));
        });

        if (!sameUpStation.isPresent()) {
            appendDownStation(upStation, downStation, distance);
        }
    }

    private void appendDownStation(Station upStation, Station downStation, int distance) {
        findSameDownStation(upStation).ifPresent(section -> {
            isGreaterThanThenPlusSortNo(section.getSortNo());
            sections.add(section.createNewAppendDownStation(distance, section.getSortNo() + 1, downStation));
        });
    }
}
