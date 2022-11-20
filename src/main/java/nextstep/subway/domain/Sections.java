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

    private boolean isFindSameStation = false;

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
        prependUpStation(upStation, downStation, distance);
        addBetweenByDownStation(upStation, downStation, distance);
        appendDownStation(upStation, downStation, distance);
    }

    public void deleteSectionByStation(Line line, Station station) {
        validateRemoveStationOfOneSection(station);

        removeLastStation(line, station);
    }

    private void validateRemoveStationOfOneSection(Station station) {
        if (sections.size() == 1) {
            findSameUpStation(station).ifPresent(section -> {
                throw new IllegalArgumentException("구간이 하나인 노선은 제거할 수 없습니다.");
            });
            findSameDownStation(station).ifPresent(section -> {
                throw new IllegalArgumentException("구간이 하나인 노선은 제거할 수 없습니다.");
            });
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
        sections.forEach(section -> section.isGreaterThanThenPlusSortNo(sortNo));
    }

    private void isGreaterThanThenMinusSortNo(int sortNo) {
        sections.forEach(section -> section.isGreaterThanThenMinusSortNo(sortNo));
    }

    private void isLessThanThenPlusSortNo(int sortNo) {
        sections.forEach(section -> section.isLessThanThenPlusSortNo(sortNo));
    }

    private void addBetweenByUpStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        findSameUpStation(upStation).ifPresent(section -> {
            section.validateLength(distance);
            isGreaterThanThenPlusSortNo(section.getSortNo());
            sections.add(section.createNewSection(distance, section.getSortNo(), upStation, downStation));
            sections.add(section.createNewSection(Math.abs(section.getDistance() - distance), section.getSortNo() + 1,
                    downStation, section.getDownStation()));
            removeSection(section);
            isFindSameStation = true;
        });
    }

    private void prependUpStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        findSameUpStation(downStation).ifPresent(section -> {
            isLessThanThenPlusSortNo(section.getSortNo());
            sections.add(section.createNewPrependUpStation(distance, section.getSortNo() - 1, upStation));
            isFindSameStation = true;
        });
    }

    private void addBetweenByDownStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        findSameDownStation(downStation).ifPresent(section -> {
            section.validateLength(distance);
            isGreaterThanThenPlusSortNo(section.getSortNo());
            sections.add(section.createNewSection(distance, section.getSortNo(), section.getUpStation(), upStation));
            sections.add(section.createNewSection(Math.abs(section.getDistance() - distance), section.getSortNo() + 1,
                    upStation, downStation));
            removeSection(section);
            isFindSameStation = true;
        });
    }

    private void appendDownStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        findSameDownStation(upStation).ifPresent(section -> {
            isGreaterThanThenPlusSortNo(section.getSortNo());
            sections.add(section.createNewAppendDownStation(distance, section.getSortNo() + 1, downStation));
            isFindSameStation = true;
        });
    }
}
