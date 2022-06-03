package nextstep.subway.domain;

import nextstep.subway.exception.SectionNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (isLineUpStation(section.getDownStation())) {
            Section lineUpSection = getLineUpSection();
            lineUpSection.updateSection(section.getUpStation(), lineUpSection.getDownStation(), section.getDistance());
            list.add(new Section(new Distance(1), null, section.getUpStation(), line));
        }
    }

    public void insertSectionWhenSectionIsTail(Line line, Section section) {
        if (isLineDownStation(section.getUpStation())) {
            Section lineDownSection = getLineDownSection();
            lineDownSection.updateSection(lineDownSection.getUpStation(), section.getDownStation(), section.getDistance());
            list.add(new Section(new Distance(1), section.getDownStation(), null, line));
        }
    }

    public void insertSectionWhenStationIsIncluded(Line line, Section section) {
        Optional<Section> upStation = findSectionWithUpStation(section.getUpStation());
        Optional<Section> downStation = findSectionWithDownStation(section.getDownStation());
        upStation.ifPresent(frontSection -> insertSectionFromFront(line, frontSection, section));
        downStation.ifPresent(rearSection -> insertSectionFromRear(line, section, rearSection));
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

    public Section getLineUpSection() {
        return list.stream()
                .filter(section -> section.getUpStation() == null)
                .findFirst()
                .orElseThrow(() -> {
                    throw new SectionNotFoundException("노선 내 구간을 찾을 수 없습니다");
                });
    }

    public Station getLineUpStation() {
        return getLineUpSection().getDownStation();
    }

    public Section getLineDownSection() {
        return list.stream()
                .filter(section -> section.getDownStation() == null)
                .findFirst()
                .orElseThrow(() -> {
                    throw new SectionNotFoundException("노선 내 구간을 찾을 수 없습니다");
                });
    }

    public Station getLineDownStation() {
        return getLineDownSection().getUpStation();
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
        List<Section> sorted = new ArrayList<>();
        Section section = getLineUpSection();
        sorted.add(section);
        while (section.getDownStation() != null) {
            section = findSectionWithUpStation(section.getDownStation()).get();
            sorted.add(section);
        }
        this.list = sorted;
    }

    @Override
    public String toString() {
        return "Sections{" +
                "list=" + list +
                '}';
    }
}
