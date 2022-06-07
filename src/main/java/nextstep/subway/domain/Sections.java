package nextstep.subway.domain;

import nextstep.subway.exception.InvalidSectionException;
import nextstep.subway.exception.SectionNotFoundException;
import nextstep.subway.exception.StationNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> list;

    public Sections() {
        list = new ArrayList<>();
    }

    public Sections(List<Section> list) {
        this.list = list;
    }

    public void addSection(Section section) {
        list.add(section);
    }

    public List<Section> getList() {
        return list;
    }

    public void insertSection(Line line, Section section) {
        insertSectionWhenSectionIsHead(line, section);
        insertSectionWhenSectionIsTail(line, section);

        if (containBothStation(section)) {
            return;
        }

        insertSectionWhenStationIsIncluded(line, section);
    }

    public void insertSectionWhenSectionIsHead(Line line, Section section) {
        Station beforeLineUpStation = getLineUpStation();
        if (beforeLineUpStation.equals(section.getDownStation())) {
            addSectionWithLine(section, line);
        }
    }

    public void insertSectionWhenSectionIsTail(Line line, Section section) {
        Station beforeLineDownStation = getLineDownStation();
        if (beforeLineDownStation.equals(section.getUpStation())) {
            addSectionWithLine(section, line);
        }
    }

    public void insertSectionWhenStationIsIncluded(Line line, Section insertSection) {
        Optional<Section> frontSection = findSectionWithUpStation(insertSection.getUpStation());
        frontSection.ifPresent(section -> insertSectionFromFront(line, section, insertSection));

        if (containBothStation(insertSection)) {
            return;
        }

        Optional<Section> rearSection = findSectionWithDownStation(insertSection.getDownStation());
        rearSection.ifPresent(section -> insertSectionFromRear(line, section, insertSection));
    }

    public void insertSectionFromFront(Line line, Section section, Section insertSection) {
        Distance restDistance = section.getDistance().minusDistance(insertSection.getDistance());
        addSectionWithLine(insertSection, line);
        addSectionWithLine(new Section(restDistance, insertSection.getDownStation(), section.getDownStation()), line);
        deleteSection(section);
    }

    public void insertSectionFromRear(Line line, Section section, Section insertSection) {
        Distance restDistance = section.getDistance().minusDistance(insertSection.getDistance());
        addSectionWithLine(insertSection, line);
        addSectionWithLine(new Section(restDistance, section.getUpStation(), insertSection.getUpStation()), line);
        deleteSection(section);
    }

    private void addSectionWithLine(Section section, Line line) {
        section.updateLine(line);
        list.add(section);
    }

    private void deleteSection(Section section) {
        list.remove(section);
        section.updateLine(null);
    }

    public void deleteSection(Line line, Station station) {
        Optional<Section> leftSection = deleteLeftSection(station);
        Optional<Section> rightSection = deleteRightSection(station);
        if (leftSection.isPresent() && rightSection.isPresent()) {
            Distance plusDistance = leftSection.get().getDistance().plusDistance(rightSection.get().getDistance());
            addSectionWithLine(new Section(plusDistance, leftSection.get().getUpStation(), rightSection.get().getDownStation()), line);
        }
    }

    private Optional<Section> deleteLeftSection(Station station) {
        Optional<Section> leftSection = findSectionWithDownStation(station);
        if (leftSection.isPresent()) {
            Section deleteSection = leftSection.get();
            deleteSection(deleteSection);
        }
        return leftSection;
    }

    private Optional<Section> deleteRightSection(Station station) {
        Optional<Section> rightSection = findSectionWithUpStation(station);
        if (rightSection.isPresent()) {
            Section deleteSection = rightSection.get();
            deleteSection(deleteSection);
        }
        return rightSection;
    }

    public boolean isLineUpStation(Station station) {
        return getLineUpStation().equals(station);
    }

    public boolean isLineDownStation(Station station) {
        return getLineDownStation().equals(station);
    }

    public Optional<Section> findSectionWithUpStation(Station upStation) {
        return list.stream()
                .filter(section -> upStation.equals(section.getUpStation()))
                .findFirst();
    }

    public Optional<Section> findSectionWithDownStation(Station downStation) {
        return list.stream()
                .filter(section -> downStation.equals(section.getDownStation()))
                .findFirst();
    }

    public Station getLineUpStation() {
        Set<Station> stationSet = getStationSet();
        this.list.forEach(section -> stationSet.remove(section.getDownStation()));
        return stationSet.stream()
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    public Station getLineDownStation() {
        Set<Station> stationSet = getStationSet();
        this.list.forEach(section -> stationSet.remove(section.getUpStation()));
        return stationSet.stream()
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    private Set<Station> getStationSet() {
        Set<Station> stationSet = new HashSet<>();
        for (Section section : this.list) {
            stationSet.add(section.getUpStation());
            stationSet.add(section.getDownStation());
        }
        return stationSet;
    }

    public Section getLineUpSection() {
        Station lineUpStation = getLineUpStation();
        return list.stream()
                .filter(section -> section.getUpStation() == lineUpStation)
                .findFirst()
                .orElseThrow(() -> {
                    throw new SectionNotFoundException("노선 내 구간을 찾을 수 없습니다");
                });
    }

    public Section getLineDownSection() {
        Station lineDownStation = getLineDownStation();
        return list.stream()
                .filter(section -> section.getDownStation() == lineDownStation)
                .findFirst()
                .orElseThrow(() -> {
                    throw new SectionNotFoundException("노선 내 구간을 찾을 수 없습니다");
                });
    }

    public boolean containStation(Station station) {
        return list.stream().anyMatch(section -> section.containsStation(station));
    }

    public boolean containBothStation(Section section) {
        return containStation(section.getUpStation()) && containStation(section.getDownStation());
    }

    public boolean containNoneStation(Section section) {
        return !containStation(section.getUpStation()) && !containStation(section.getDownStation());
    }

    public Sections getSortedSections() {
        Section currentSection = findSectionWithUpStation(getLineUpStation()).orElseThrow(SectionNotFoundException::new);
        Section tailSection = findSectionWithDownStation(getLineDownStation()).orElseThrow(SectionNotFoundException::new);
        List<Section> sorted = new ArrayList<>();
        sorted.add(currentSection);
        while (currentSection != tailSection) {
            currentSection = findSectionWithUpStation(currentSection.getDownStation()).orElseThrow(SectionNotFoundException::new);
            sorted.add(currentSection);
        }
        return new Sections(sorted);
    }

    public List<Station> getSortedLineStations() {
        List<Section> sectionList = getSortedSections().getList();
        Station lineUpStation = sectionList.get(0).getUpStation();
        List<Station> stationList = new ArrayList<>();
        stationList.add(lineUpStation);
        for (Section section : sectionList) {
            stationList.add(section.getDownStation());
        }
        return stationList;
    }

    public void validateInsertSection(Section section) {
        if (containBothStation(section)) {
            throw new InvalidSectionException("이미 노선에 포함된 구간은 추가할 수 없습니다.");
        }

        if (containNoneStation(section)) {
            throw new InvalidSectionException("구간 내 지하철 역이 하나는 등록된 상태여야 합니다.");
        }
    }

    public void validateDeleteSection(Station station) {
        if (getList().size() == 1) {
            throw new InvalidSectionException("하나만 남은 구간은 삭제할 수 없습니다.");
        }

        if (!containStation(station)) {
            throw new InvalidSectionException("하나만 남은 구간은 삭제할 수 없습니다.");
        }
    }

    @Override
    public String toString() {
        return "Sections{" +
                "list=" + list +
                '}';
    }
}
