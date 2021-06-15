package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineSections {
    private final static String NOT_EXISTS_STATION = "존재하지 않는 역입니다.";
    private final static String CAN_NOT_DELETE_SECTION = "등록된 구간이 유일하여 역을 삭제할 수 없습니다";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> lineSections = new ArrayList<>();

    public LineSections(List<Section> lineSections) {
        this.lineSections = lineSections;
    }

    public LineSections() {
    }

    public void add(Section section) {
        lineSections.add(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(lineSections);
    }

    public List<Station> toStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : lineSections) {
            stations.addAll(section.toStations());
        }
        return stations;
    }

    public List<Section> getOrderedLineSections() {
        List<Section> orderedSections = new ArrayList<>();
        orderedSections.add(findFirstSection());

        for (int i=0; i<lineSections.size() - 1; i++) {
            Section curSection = orderedSections.get(orderedSections.size() - 1);
            Section findSection = findSameUpStationSection(curSection.getDownStation());
            orderedSections.add(findSection);
        }
        return orderedSections;
    }

    private Section findSameUpStationSection(Station station) {
        return lineSections.stream()
            .filter(section -> section.getUpStation().isSame(station))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("해당 역을 가지는 노선이 존재하지 않습니다."));
    }

    private Section findFirstSection() {
        return lineSections.stream()
            .filter(section -> !isExistsDownStation(section.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("노선의 시작역이 존재하지 않습니다."));
    }

    private boolean isExistsDownStation(Station station) {
        return lineSections.stream()
            .filter(section -> station.isSame(section.getDownStation()))
            .findFirst()
            .isPresent();
    }

    public List<Station> getOrderedStation() {
        List<Station> orderStations = new ArrayList<>();
        Section curSection = findFirstSection();
        orderStations.add(curSection.getUpStation());
        orderStations.add(curSection.getDownStation());

        for (int i=0; i<lineSections.size() - 1; i++) {
            curSection = findSameUpStationSection(curSection.getDownStation());
            orderStations.add(curSection.getDownStation());
        }
        return orderStations;
    }

    private void validateLineSectionsHasOnly() {
        if (lineSections.size() <= 1) {
            throw new RuntimeException(CAN_NOT_DELETE_SECTION);
        }
    }

    public void removeSectionByStation(Station station) {
        validateLineSectionsHasOnly();
        Section removeSection = findSectionToRemove(station);
        updateSectionByRemove(station, removeSection);
        lineSections.remove(removeSection);
    }

    private Section findSectionToRemove(Station station) {
        return lineSections.stream()
            .filter(section -> section.isContainStation(station))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(NOT_EXISTS_STATION));
    }

    private void updateSectionByRemove(Station station, Section removeSection) {
        lineSections.stream()
            .filter(section -> section.getUpStation().isSame(station))
            .findFirst()
            .ifPresent(section -> {
                section.changeUpStation(removeSection.getUpStation());
                section.addDistance(removeSection.getDistance());
            });
    }
}
