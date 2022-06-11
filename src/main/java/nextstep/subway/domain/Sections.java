package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sectionList = new ArrayList<>();

    protected Sections() {

    }

    public long getDistance() {
        return sectionList.stream().mapToLong(Section::getDistance).sum();
    }

    public List<Section> getSectionList() {
        List<Section> sortedList = new ArrayList<>();
        Station upStation = getUpStation();

        Optional<Section> firstSection = findSectionByUpStation(upStation);
        if (!firstSection.isPresent()) {
            return sortedList;
        }

        sortedList.add(firstSection.get());
        Station nextStation = firstSection.get().getDownStation();
        while (nextStation != null) {
            Optional<Section> section = findSectionByUpStation(nextStation);
            if (!section.isPresent()) {
                break;
            }

            sortedList.add(section.get());
            nextStation = section.get().getDownStation();
        }

        return sortedList;
    }

    public void add(Section newSection) {
        validateStations(newSection.getUpStation(), newSection.getDownStation());
        if (hasSameUpStation(newSection.getDownStation()) && !hasSameDownStation(newSection.getUpStation())) {
            sectionList.add(newSection);
            return ;
        }

        if (hasSameDownStation(newSection.getUpStation()) && !hasSameUpStation(newSection.getDownStation())) {
            sectionList.add(newSection);
            return ;
        }

        addSectionBetweenTwoStation(newSection);
    }

    public void addFirstSection(Section section) {
        if (!sectionList.isEmpty()) {
            throw new IllegalStateException("이미 구간이 등록된 경우 추가할 수 없습니다.");
        }
        sectionList.add(section);
    }

    public void addSectionBetweenTwoStation(Section newSection) {
        Optional<Section> sameUpSection = findSectionByUpStation(newSection.getUpStation());
        if (sameUpSection.isPresent()) {
            Section existingSection = sameUpSection.get();
            changeSectionFromUpStation(newSection, existingSection);
            return ;
        }

        Optional<Section> sameDownSection = findSectionByDownStation(newSection.getDownStation());
        if (sameDownSection.isPresent()) {
            Section existingSection = sameDownSection.get();
            changeSectionFromDownStation(newSection, existingSection);
        }
    }

    private void changeSectionFromDownStation(Section newSection, Section existingSection) {
        validateNewSectionDistance(newSection.getDistance(), existingSection.getDistance());
        Section nextSection = new Section(newSection.getUpStation(), existingSection.getDownStation()
                , newSection.getDistance());
        sectionList.add(nextSection);
        existingSection.setDownStation(newSection.getUpStation());
        existingSection.setDistance(existingSection.getDistance() - newSection.getDistance());
    }

    private void changeSectionFromUpStation(Section newSection, Section existingSection) {
        validateNewSectionDistance(newSection.getDistance(), existingSection.getDistance());

        Section nextSection = new Section(newSection.getDownStation(), existingSection.getDownStation()
                , existingSection.getDistance() - newSection.getDistance());
        sectionList.add(nextSection);
        existingSection.setDownStation(newSection.getDownStation());
        existingSection.setDistance(newSection.getDistance());
    }

    public Station getUpStation() {
        List<Station> downStations = sectionList.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        Optional<Section> upStationSection = sectionList.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst();

        return upStationSection.map(Section::getUpStation).orElse(null);

    }

    public Station getDownStation() {
        List<Station> upStaions = sectionList.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        Optional<Section> downStationSection = sectionList.stream()
                .filter(section -> !upStaions.contains(section.getDownStation()))
                .findFirst();

        return downStationSection.map(Section::getUpStation).orElse(null);
    }

    public Optional<Section> findSectionByUpStation(Station upStation) {
        return sectionList.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();
    }

    public Optional<Section> findSectionByDownStation(Station downStation) {
        return sectionList.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();
    }

    public boolean hasSameUpStation(Station upStation) {
        return findSectionByUpStation(upStation).isPresent();
    }

    public boolean hasSameDownStation(Station downStation) {
        return findSectionByDownStation(downStation).isPresent();
    }

    private void validateNewSectionDistance(long newSectionDistance, long existingSectionDistance) {
        if (newSectionDistance >= existingSectionDistance) {
            throw new IllegalArgumentException("등록하고자 하는 구간의 거리가 역 사이 길이보다 크거나 같습니다.");
        }
    }

    private void validateStations(Station newUpStation, Station newDownStation) {
        if (hasStation(newUpStation) && hasStation(newDownStation)) {
            throw new IllegalArgumentException("두 역 모두 이미 등록된 역입니다.");
        }

        if (!hasStation(newUpStation) && !hasStation(newDownStation)) {
            throw new IllegalArgumentException("두 역 모두 등록되지 않은 역입니다.");
        }
    }

    private boolean hasStation(Station station) {
        return getSectionList().stream()
                .anyMatch(section -> section.getUpStation().equals(station) || section.getDownStation().equals(station));
    }

    public void removeSectionByStation(Station station) {
        validateRemoveStatus();
        Optional<Section> fromStation = findSectionByUpStation(station);
        Optional<Section> toStation = findSectionByDownStation(station);

        if (fromStation.isPresent() && toStation.isPresent()) {
            Station newUpStation = toStation.get().getUpStation();
            Station newDownStation = fromStation.get().getDownStation();
            long newDistance = fromStation.get().getDistance() + toStation.get().getDistance();
            sectionList.remove(fromStation.get());
            sectionList.remove(toStation.get());
            sectionList.add(new Section(newUpStation, newDownStation, newDistance));
        }

    }

    private void validateRemoveStatus() {
        if (sectionList.size() <= 1) {
            throw new IllegalStateException("마지막 구간은 제거할 수 없습니다.");
        }
    }
}
