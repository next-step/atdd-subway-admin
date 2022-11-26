package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    public static final String SECTION_DUPLICATE_EXCEPTION_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.";
    public static final String SECTION_CONTAINS_EXCEPTION_MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.";
    public static final String DISTANCE_MINIMUM_EXCEPTION_MESSAGE = "새로운 구간의 거리가 기존 구간의 거리보다 크거나 같으면 등록을 할 수 없다.";
    public static final int REMOVE_SECTION_MINIMUM_SIZE = 1;
    public static final int SECTION_MINIMUM_SIZE = 1;
    public static final int ADD_INIT_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section addSection) {
        validateStations(addSection);
        if (isAddBetweenSection(addSection)) {
            addSplitSection(addSection, removeSection(addSection));
        }
        this.sections.add(addSection);
    }

    private void addSplitSection(Section addSection, Section removeSection) {
        if (addSection.isSameUpStation(removeSection)) {
            addDownSection(addSection, removeSection);
        }
        if (addSection.isSameDownStation(removeSection)) {
            addUpSection(addSection, removeSection);
        }
    }

    private void addDownSection(Section addSection, Section removeSection) {
        validateAddSectionDistance(addSection, removeSection);
        this.sections.add(new Section(addSection.getLine(), addSection.getDownStation(), removeSection.getDownStation(), removeSection.differ(addSection)));
    }

    private void addUpSection(Section addSection, Section removeSection) {
        validateAddSectionDistance(addSection, removeSection);
        this.sections.add(new Section(addSection.getLine(), removeSection.getUpStation(), addSection.getUpStation(), removeSection.differ(addSection)));
    }

    private boolean isExistsAllStations(Section section) {
        return this.sections.stream()
                .anyMatch(value -> section.isUpStation(value) && section.isDownStation(value));
    }

    private boolean isContainStation(Section section) {
        return this.sections.stream()
                .anyMatch(value -> section.isUpStation(value) || section.isDownStation(value));
    }

    private boolean isSameUpStation(Section section) {
        return this.sections.stream()
                .anyMatch(section1 -> section1.isSameUpStation(section));
    }

    private boolean isSameDownStation(Section section) {
        return this.sections.stream()
                .anyMatch(section1 -> section1.isSameDownStation(section));
    }

    private boolean isSameUpStation(Station station) {
        return this.sections.stream()
                .anyMatch(section1 -> section1.isUpStation(station));
    }

    private boolean isSameDownStation(Station station) {
        return this.sections.stream()
                .anyMatch(section1 -> section1.isDownStation(station));
    }

    private boolean isAddBetweenSection(Section section) {
        if (findSize() < SECTION_MINIMUM_SIZE) {
            return false;
        }
        return isSameUpStation(section) || isSameDownStation(section);
    }

    private boolean isLastDownStation(Station upStation) {
        return this.sections.stream()
                .noneMatch(section -> section.isUpStation(upStation));
    }

    private void addStations(List<Station> stations) {
        Station upStation = findDownStation(findFirstUpStation());
        while (!isLastDownStation(upStation)) {
            addStation(stations, upStation);
            upStation = findDownStation(upStation);
        }
    }

    private void addFirstSectionStations(List<Station> stations) {
        Station firstUpStation = findFirstUpStation();
        stations.add(firstUpStation);
        stations.add(findDownStation(firstUpStation));
    }

    private void addStation(List<Station> stations, Station upStation) {
        this.sections.stream()
                .filter(section -> section.isUpStation(upStation))
                .map(Section::getDownStation)
                .forEach(stations::add);
    }

    private void addUniteSection(Section upSection, Section downSection) {
        this.sections.add(new Section(upSection.getLine(), upSection.getUpStation(), downSection.getDownStation(), upSection.sumDistance(downSection)));
    }

    private Section findUpSection(Station station) {
        for (Section section : this.sections) {
            if (section.isDownStation(station)) {
                return section;
            }
        }
        throw new IllegalArgumentException();
    }

    private Section findDownSection(Station station) {
        for (Section section : this.sections) {
            if (section.isUpStation(station)) {
                return section;
            }
        }
        throw new IllegalArgumentException();
    }

    private Station findDownStation(Station station) {
        for (Section section : this.sections) {
            if (section.isUpStation(station)) {
                return section.getDownStation();
            }
        }
        throw new IllegalArgumentException();
    }

    private Station findFirstUpStation() {
        Station firstUpStation = this.sections.get(0).getUpStation();
        for (Section section : this.sections) {
            if (section.isDownStation(firstUpStation)) {
                firstUpStation = section.getUpStation();
            }
        }
        return firstUpStation;
    }

    private Section findSameSection(Section section) {
        Section removeSection = null;
        if (isSameUpStation(section)) {
            removeSection = findSameUpStationSection(section);
        }
        if (isSameDownStation(section)) {
            removeSection = findSameDownStationSection(section);
        }
        return removeSection;
    }

    private Section findSameUpStationSection(Section addSection) {
        for (Section section : this.sections) {
            if (section.isSameUpStation(addSection)) {
                return section;
            }
        }
        throw new IllegalArgumentException();
    }

    private Section findSameDownStationSection(Section section) {
        for (Section value : this.sections) {
            if (value.isSameDownStation(section)) {
                return value;
            }
        }
        throw new IllegalArgumentException();
    }

    public int findSize() {
        return this.sections.size();
    }

    public List<Station> findStations() {
        List<Station> stations = new ArrayList<>();
        addFirstSectionStations(stations);
        addStations(stations);
        return stations;
    }

    public int findDistance() {
        return this.sections.stream()
                .mapToInt(section -> section.getDistance().getDistance())
                .sum();
    }

    public void removeStation(Station station) {
        validateRemoveStation(station);
        remove(station);
    }

    private void remove(Station station) {
        if (isSameDownStation(station) && isSameUpStation(station)) {
            addUniteSection(removeUpSection(station), removeDownSection(station));
            return;
        }
        if (isSameDownStation(station)) {
            removeUpSection(station);
        }
        if (isSameUpStation(station)) {
            removeDownSection(station);
        }
    }

    private Section removeSection(Section section) {
        Section removeSection = findSameSection(section);
        this.sections.remove(removeSection);
        return removeSection;
    }

    private Section removeUpSection(Station station) {
        Section section = findUpSection(station);
        this.sections.remove(section);
        return section;
    }

    private Section removeDownSection(Station station) {
        Section section = findDownSection(station);
        this.sections.remove(section);
        return section;
    }

    private void validateAddSectionDistance(Section addSection, Section removeSection) {
        if (!removeSection.isLonger(addSection)) {
            throw new IllegalArgumentException(DISTANCE_MINIMUM_EXCEPTION_MESSAGE);
        }
    }

    private void validateStations(Section addSection) {
        if (findSize() < ADD_INIT_SIZE) {
            return;
        }
        validateExistsAllStations(addSection);
        validateNotExistsAllStations(addSection);
    }

    private void validateExistsAllStations(Section section) {
        if (isExistsAllStations(section)) {
            throw new IllegalArgumentException(SECTION_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    private void validateNotExistsAllStations(Section section) {
        if (!isContainStation(section)) {
            throw new IllegalArgumentException(SECTION_CONTAINS_EXCEPTION_MESSAGE);
        }
    }

    private void validateRemoveStation(Station station) {
        validateLastSection();
        validateNotExistsAllStations(station);
    }

    private void validateLastSection() {
        if (findSize() <= REMOVE_SECTION_MINIMUM_SIZE) {
            throw new IllegalArgumentException();
        }
    }

    private void validateNotExistsAllStations(Station station) {
        if (!isSameDownStation(station) && !isSameUpStation(station)) {
            throw new IllegalArgumentException();
        }
    }
}
