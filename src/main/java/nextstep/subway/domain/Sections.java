package nextstep.subway.domain;

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
            addSectionWithLine(line, section);
        }
    }

    public void insertSectionWhenSectionIsTail(Line line, Section section) {
        Station beforeLineDownStation = getLineDownStation();
        if (beforeLineDownStation.equals(section.getUpStation())) {
            addSectionWithLine(line, section);
        }
    }

    private void addSectionWithLine(Line line, Section section) {
        section.updateLine(line);
        list.add(section);
    }

    public void insertSectionWhenStationIsIncluded(Line line, Section section) {
        Optional<Section> frontSection = findSectionWithUpStation(section.getUpStation());
        Optional<Section> rearSection = findSectionWithDownStation(section.getDownStation());
        frontSection.ifPresent(front -> insertSectionFromFront(line, front, section));
        rearSection.ifPresent(rear -> insertSectionFromRear(line, section, rear));
    }

    public void insertSectionFromFront(Line line, Section frontSection, Section rearSection) {
        Distance restDistance = frontSection.getDistance().minusDistance(rearSection.getDistance());
        list.add(new Section(restDistance, rearSection.getDownStation(), frontSection.getDownStation(), line));
        frontSection.updateSection(frontSection.getUpStation(), rearSection.getDownStation(), rearSection.getDistance());
    }

    public void insertSectionFromRear(Line line, Section frontSection, Section rearSection) {
        Distance restDistance = rearSection.getDistance().minusDistance(frontSection.getDistance());
        list.add(new Section(frontSection.getDistance(), frontSection.getUpStation(), rearSection.getDownStation(), line));
        rearSection.updateSection(rearSection.getUpStation(), frontSection.getUpStation(), restDistance);
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
        Set<Station> staionSet = new HashSet<>();
        for (Section section : this.list) {
            staionSet.add(section.getUpStation());
            staionSet.add(section.getDownStation());
        }

        for (Section section : this.list) {
            staionSet.remove(section.getDownStation());
        }

        return staionSet.stream()
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    /*
    public Station getLineUpStation() {
        return getLineUpSection().getDownStation();
    }
    */

    public Section getLineUpSection() {
        Station lineUpStation = getLineUpStation();
        return list.stream()
                .filter(section -> section.getUpStation() == lineUpStation)
                .findFirst()
                .orElseThrow(() -> {
                    throw new SectionNotFoundException("노선 내 구간을 찾을 수 없습니다");
                });
    }

    public Station getLineDownStation() {
        Set<Station> staionSet = new HashSet<>();
        for (Section section : this.list) {
            staionSet.add(section.getUpStation());
            staionSet.add(section.getDownStation());
        }

        for (Section section : this.list) {
            staionSet.remove(section.getUpStation());
        }

        return staionSet.stream()
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    /*
    public Station getLineDownStation() {
        return getLineDownSection().getUpStation();
    }
     */

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

    public void sort() {
        Section head = findSectionWithUpStation(getLineUpStation())
                .orElseThrow(SectionNotFoundException::new);
        Section tail = findSectionWithDownStation(getLineDownStation())
                .orElseThrow(SectionNotFoundException::new);

        if (head == tail) {
            return;
        }

        List<Section> sorted = new ArrayList<>();
        sorted.add(head);
        do {
            Section find = findSectionWithUpStation(head.getDownStation())
                    .orElseThrow(SectionNotFoundException::new);
            sorted.add(find);
            head = findSectionWithUpStation(head.getDownStation())
                    .orElseThrow(SectionNotFoundException::new);
        } while (head != tail);
        this.list = sorted;
    }

    public List<Station> getSortedLineStations() {
        sort();
        List<Station> stations = new ArrayList<>();
        stations.add(this.list.get(0).getUpStation());
        for (Section section : this.list) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    @Override
    public String toString() {
        return "Sections{" +
                "list=" + list +
                '}';
    }
}
