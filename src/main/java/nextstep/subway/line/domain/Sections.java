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

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section addSection) {
        validateStations(addSection);
        if (isAddBetweenSection(addSection)) {
            addSplitSection(addSection, removeSection(addSection));
        }
        this.sections.add(addSection);
    }

    private void validateStations(Section addSection) {
        if (size() < 1) {
            return;
        }
        validateExistsAllStations(addSection);
        validateNotExistsAllStations(addSection);
    }

    private void validateExistsAllStations(Section section) {
        if (getStations().contains(section.getUpStation()) && getStations().contains(section.getDownStation())) {
            throw new IllegalArgumentException(SECTION_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    private void validateNotExistsAllStations(Section section) {
        if (!isContainUpStation(section) && !isContainDownStation(section)) {
            throw new IllegalArgumentException(SECTION_CONTAINS_EXCEPTION_MESSAGE);
        }
    }

    private boolean isAddBetweenSection(Section addSection) {
        if (size() < 1) {
            return false;
        }
        return isSameUpStation(addSection.getUpStation()) || isSameDownStation(addSection.getDownStation());
    }

    private boolean isSameUpStation(Station upStation) {
        return this.sections.stream()
                .anyMatch(section -> section.isUpStation(upStation));
    }

    private boolean isSameDownStation(Station downStation) {
        return this.sections.stream()
                .anyMatch(section -> section.isDownStation(downStation));
    }

    private Section removeSection(Section section) {
        if (isSameUpStation(section.getUpStation())) {
            Section removeSection = findSameUpStationSection(section);
            this.sections.remove(removeSection);
            return removeSection;
        }
        if (isSameDownStation(section.getDownStation())) {
            Section removeSection = findSameDownStationSection(section.getDownStation());
            this.sections.remove(removeSection);
            return removeSection;
        }
        throw new IllegalArgumentException();
    }

    private Section findSameUpStationSection(Section addSection) {
        for (Section section : this.sections) {
            if (section.isUpStation(addSection.getUpStation())) {
                return section;
            }
        }
        throw new IllegalArgumentException();
    }

    private Section findSameDownStationSection(Station downStation) {
        for (Section section : this.sections) {
            if (section.isDownStation(downStation)) {
                return section;
            }
        }
        throw new IllegalArgumentException();
    }

    private void addSplitSection(Section addSection, Section removeSection) {
        if (addSection.isUpStation(removeSection.getUpStation())) {
            addDownSection(addSection, removeSection);
        }
        if (addSection.isDownStation(removeSection.getDownStation())) {
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

    private void validateAddSectionDistance(Section addSection, Section removeSection) {
        if (!removeSection.isLonger(addSection)) {
            throw new IllegalArgumentException(DISTANCE_MINIMUM_EXCEPTION_MESSAGE);
        }
    }

    private boolean isContainDownStation(Section section) {
        return getStations().contains(section.getDownStation());
    }

    private boolean isContainUpStation(Section section) {
        return getStations().contains(section.getUpStation());
    }

    public int size() {
        return this.sections.size();
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        addFirstSectionStations(stations);
        addStations(stations);
        return stations;
    }

    private void addStations(List<Station> stations) {
        Station upStation = findDownStationByUpStation(findFirstUpStation());
        while (!isLastDownStation(upStation)) {
            addStation(stations, upStation);
            upStation = findDownStationByUpStation(upStation);
        }
    }

    private Station findDownStationByUpStation(Station upStation) {
        Station downStation = upStation;
        for (Section section : this.sections) {
            downStation = findDownStationByUpStation(upStation, downStation, section);
        }
        return downStation;
    }

    private boolean isLastDownStation(Station upStation) {
        return this.sections.stream()
                .noneMatch(section -> section.isUpStation(upStation));
    }

    private void addFirstSectionStations(List<Station> stations) {
        Station firstUpStation = findFirstUpStation();
        stations.add(firstUpStation);
        stations.add(findDownStationByUpStation(firstUpStation));
    }

    private Station findFirstUpStation() {
        Station firstUpStation = this.sections.get(0).getUpStation();
        for (Section section : this.sections) {
            firstUpStation = findUpStation(firstUpStation, section);
        }
        return firstUpStation;
    }

    private static Station findDownStationByUpStation(Station upStation, Station downStation, Section section) {
        Station station = downStation;
        if (section.isUpStation(upStation)) {
            station = section.getDownStation();
        }
        return station;
    }

    private void addStation(List<Station> stations, Station upStation) {
        this.sections.stream()
                .filter(section -> section.isUpStation(upStation))
                .map(Section::getDownStation)
                .forEach(stations::add);
    }

    private static Station findUpStation(Station firstUpStation, Section section) {
        Station upStation = firstUpStation;
        if (section.isDownStation(firstUpStation)) {
            upStation = section.getUpStation();
        }
        return upStation;
    }

    public int getDistance() {
        return this.sections.stream()
                .mapToInt(section -> section.getDistance().getDistance())
                .sum();
    }
}
