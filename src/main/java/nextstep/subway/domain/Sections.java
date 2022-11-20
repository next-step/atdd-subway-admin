package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    private boolean isFindSameStation = false;

    public List<Section> getSections() {
        return sections;
    }

    public List<Integer> getSortedDistances() {
        ascendingSectionsBySortNo();
        return sections.stream().map(Section::getDistance).collect(Collectors.toList());
    }

    public Set<String> getSortedStationNames() {
        Set<String> stationNames = new LinkedHashSet<>();
        ascendingSectionsBySortNo();
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

    public void deleteSectionByStation(Station station) {
        ascendingSectionsBySortNo();
        ifLastSectionAndDownStationThenRemove(station);
    }

    private void ifLastSectionAndDownStationThenRemove(Station station) {
        Section lastSection = sections.get(sections.size() - 1);
        if (lastSection.isEqualsDownStation(station)) {
            removeSection(lastSection);
        }
    }

    private void ascendingSectionsBySortNo() {
        sections.sort((o1, o2) -> o1.getSortNo() < o2.getSortNo() ? -1 : 0);
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
