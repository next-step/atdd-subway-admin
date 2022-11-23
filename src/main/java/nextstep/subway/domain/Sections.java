package nextstep.subway.domain;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.value.ErrMsg;

@Embeddable
public class Sections {

    private static final int NON_DELETABLE_SECTION_COUNT = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = Lists.newArrayList();


    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        validateSection(section);
        if (!addFirstOrLastSection(section)) {
            addSectionInMiddle(section);
        }
    }

    public List<Station> getOrderedStations() {
        Station currentStation = getFirstUpStation();
        List<Station> orderedStations = new ArrayList<>();
        orderedStations.add(currentStation);
        while (orderedStations.size() - 1 < sections.size()) {
            currentStation = getUpMatchingSection(currentStation).get().getDownStation();
            orderedStations.add(currentStation);
        }
        return Collections.unmodifiableList(orderedStations);
    }

    public Optional<Section> getSectionById(Long sectionId) {
        return sections.stream().filter(section -> section.getId().equals(sectionId)).findFirst();
    }

    public List<Section> getAll() {
        return sections;
    }


    public void deleteStation(Station station) throws NotFoundException {
        validateDeleteCondition();
        if (!deleteFirstOrLastStation(station)) {
            deleteMiddleStation(station);
        }
    }

    private void validateSection(Section section) {
        List<Station> stations = getAllStations();
        if (isContain(stations, section)) {
            throw new IllegalArgumentException(ErrMsg.SECTION_ALREADY_EXISTS);
        }
        if (isNotContain(stations, section)) {
            throw new IllegalArgumentException(ErrMsg.NO_MATCHING_STATIONS);
        }
    }

    private boolean isContain(List<Station> stations, Section section) {
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }

    private boolean isNotContain(List<Station> stations, Section section) {
        return !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation());
    }

    private void addSectionInMiddle(Section section) {
        if (!addUpMatchingSection(section)) {
            addDownMatchingSection(section);
        }
    }

    private boolean addUpMatchingSection(Section section) {
        Optional<Section> result = getUpMatchingSection(section.getUpStation());
        if (result.isPresent()) {
            result.get().updateUpStation(section);
            sections.add(section);
            return true;
        }
        return false;
    }


    private void addDownMatchingSection(Section section) {
        Section target = getDownMatchingSection(section.getDownStation()).get();
        target.updateDownStation(section);
        sections.add(section);
    }

    private Optional<Section> getUpMatchingSection(Station station) {
        return sections.stream().filter(
                section -> section.getUpStation().equals(station)
        ).findFirst();
    }

    private Optional<Section> getDownMatchingSection(Station station) {
        return sections.stream().filter(
                section -> section.getDownStation().equals(station)
        ).findFirst();
    }

    private boolean addFirstOrLastSection(Section section) {
        if (isFirstOrLastFromSection(section)) {
            sections.add(section);
            return true;
        }
        return false;
    }

    private boolean isFirstOrLastFromSection(Section section) {
        return getLastDownStation().equals(section.getUpStation()) || getFirstUpStation().equals(
                section.getDownStation());
    }

    private List<Station> getAllStations() {
        return sections.stream().map(Section::getStations).flatMap(Collection::stream).distinct()
                .collect(Collectors.toList());
    }


    private List<Station> getUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private Station getFirstUpStation() {
        // 상행 종점 구하기
        List<Station> stations = getUpStations();
        stations.removeAll(getDownStations());
        return stations.get(0);
    }

    private Station getLastDownStation() {
        // 하행 종점 구하기
        List<Station> stations = getDownStations();
        stations.removeAll(getUpStations());
        return stations.get(0);
    }


    private void deleteMiddleStation(Station station) {
        getUpMatchingSection(station).ifPresent(it -> deleteMiddleSection(it));
    }

    private void deleteMiddleSection(Section it) {
        Section target = getDownMatchingSection(it.getUpStation()).get();
        target.updateSectionInDelete(it);
        this.sections.remove(it);
    }

    private void validateDeleteCondition() {
        if (sections.size() <= NON_DELETABLE_SECTION_COUNT) {
            throw new IllegalStateException(ErrMsg.CANNOT_DELETE_SECTION_WHEN_ONE);
        }
    }


    private boolean deleteFirstOrLastStation(Station station) {
        if (getFirstUpStation().equals(station)) {
            sections.remove(0);
            return true;
        }
        if (getLastDownStation().equals(station)) {
            sections.remove(sections.size() - 1);
            return true;
        }
        return false;
    }
}
