package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        List<Integer> distances = new ArrayList<>();

        sections.stream()
                .filter(Section::isFirstSection)
                .findFirst()
                .ifPresent(section -> {
                    distances.add(section.getDistance());
                    section.ifExistNextSectionThenAddDistances(distances);
                });

        return distances;
    }

    public Set<String> getStationNames() {
        Set<String> stationNames = new LinkedHashSet<>();

        sections.stream()
                .filter(Section::isFirstSection)
                .findFirst()
                .ifPresent(section -> {
                    stationNames.add(section.getUpStation().getName());
                    stationNames.add(section.getDownStation().getName());
                    section.ifExistNextSectionThenAddStationNames(stationNames);
                });

        return stationNames;
    }

    public void addDefaultSection(Line line, int distance, Station upStation, Station downStation) {
        sections.add(new Section(line, distance, upStation, downStation));
    }

    public void removeSection(Section section) {
        section.removeSection();
        sections.remove(section);
    }

    public void addSection(int distance, Station upStation, Station downStation) {
        validateAlreadyExistsStation(upStation, downStation);
        validateNotExistsStation(upStation, downStation);

        addBetweenByUpStation(upStation, downStation, distance);
    }

    public void deleteSectionByStation(Line line, Station station) {
        validateRemoveStationOfOneSection(station);

        removeEndStation(line, station);
    }

    private void validateRemoveStationOfOneSection(Station station) {
        if (sections.size() == 1 && isFindSameStation(station)) {
            throw new IllegalArgumentException("구간이 하나인 노선은 제거할 수 없습니다.");
        }
    }

    private void removeBetweenStation(Line line, Station station) {
        Section findSameDownStation = findSameDownStation(station)
                .orElseThrow(EntityNotFoundException::new);
        Section findSameUpStation = findSameUpStation(station)
                .orElseThrow(EntityNotFoundException::new);

        sections.add(deleteBetweenStationAndCreateNewSection(line, findSameDownStation, findSameUpStation));
    }

    private Section deleteBetweenStationAndCreateNewSection(Line line, Section findSameDownStation,
                                                            Section findSameUpStation) {

        Section newSection = new Section(line, findSameDownStation.getDistance() + findSameUpStation.getDistance(),
                findSameDownStation.getUpStation(), findSameUpStation.getDownStation());

        findSameDownStation.ifExistPreSectionThenSetNextSection(newSection);
        findSameUpStation.ifExistNextSectionThenSetPreSection(newSection);
        removeSection(findSameDownStation);
        removeSection(findSameUpStation);

        return newSection;
    }

    private void removeEndStation(Line line, Station station) {
        ifFindFirstSectionThenRemove(line, station);
    }

    private void ifFindFirstSectionThenRemove(Line line, Station station) {
        Optional<Section> firstSection = findUpStationOfFirstSection(station);
        firstSection.ifPresent(this::removeSection);

        if (!firstSection.isPresent()) {
            ifFindLastSectionThenRemove(line, station);
        }
    }

    private void ifFindLastSectionThenRemove(Line line, Station station) {
        Optional<Section> lastSection = findDownStationOfLastSection(station);
        lastSection.ifPresent(this::removeSection);

        if (!lastSection.isPresent()) {
            removeBetweenStation(line, station);
        }
    }

    private Optional<Section> findUpStationOfFirstSection(Station station) {
        return sections.stream()
                .filter(section -> section.isFirstSection() && section.isEqualsUpStation(station))
                .findFirst();
    }

    private Optional<Section> findDownStationOfLastSection(Station station) {
        return sections.stream()
                .filter(section -> section.isLastSection() && section.isEqualsDownStation(station))
                .findFirst();
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
        sections.forEach(section -> section
                .validateAlreadyExistsStation(upStation, downStation));
    }

    private void validateNotExistsStation(Station upStation, Station downStation) {
        if (!isFindSameStation(upStation) && !isFindSameStation(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    private void addBetweenByUpStation(Station upStation, Station downStation, int distance) {
        Optional<Section> sameUpStation = findSameUpStation(upStation);
        sameUpStation.ifPresent(section -> {
            section.validateLength(distance);
            sections.add(section.createBetweenSectionByUpStation(distance, upStation, downStation));
        });

        if (!sameUpStation.isPresent()) {
            addBetweenByDownStation(upStation, downStation, distance);
        }
    }

    private void addBetweenByDownStation(Station upStation, Station downStation, int distance) {
        Optional<Section> sameDownStation = findSameDownStation(downStation);
        sameDownStation.ifPresent(section -> {
            section.validateLength(distance);
            sections.add(section.createBetweenSectionByDownStation(distance, upStation, downStation));
        });

        if (!sameDownStation.isPresent()) {
            prependUpStation(upStation, downStation, distance);
        }
    }

    private void prependUpStation(Station upStation, Station downStation, int distance) {
        Optional<Section> sameUpStation = findSameUpStation(downStation);
        sameUpStation.ifPresent(section -> {
            sections.add(section.createPrependSection(distance, upStation, downStation));
        });

        if (!sameUpStation.isPresent()) {
            appendDownStation(upStation, downStation, distance);
        }
    }

    private void appendDownStation(Station upStation, Station downStation, int distance) {
        findSameDownStation(upStation).ifPresent(section -> {
            sections.add(section.createAppendSection(distance, upStation, downStation));
        });
    }
}
