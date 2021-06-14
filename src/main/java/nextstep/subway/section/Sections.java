package nextstep.subway.section;

import nextstep.subway.exception.CannotDeleteException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    private final int MINIMUM_REMOVABLE_SIZE = 2;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    public void add(Section section) {
        if (sections.contains(section)) {
            return;
        }

        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        validateStations(section);
        connectIfFront(section);
        connectIfLast(section);
        connectToExistingUpStation(section);
        connectToExistingDownStation(section);
    }

    private void validateStations(Section section) {
        List<Station> stations = getStations();
        checkUpAndDownStationAlreadyContained(stations, section);
        checkUpandDownStationNotContained(stations, section);
    }

    private void checkUpandDownStationNotContained(List<Station> stations, Section section) {
        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("구간의 상하행역중 하나라도 등록이 되어 있어야 합니다.");
        }
    }

    private void checkUpAndDownStationAlreadyContained(List<Station> stations, Section section) {
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("구간의 상하행역이 이미 모두 등록되어있습니다.");
        }
    }

    private void connectToExistingDownStation(Section section) {
        if (sections.contains(section)) {
            return;
        }
        Section existingSection = sections.stream()
                .filter(it -> it.getDownStation().equals(section.getDownStation()))
                .findFirst()
                .orElse(null);
        if (existingSection != null) {
            int indexOfExisting = sections.indexOf(existingSection);
            existingSection.connectDownStationTo(section);
            sections.add(indexOfExisting + 1, section);
        }
    }

    private void connectToExistingUpStation(Section section) {
        if (sections.contains(section)) {
            return;
        }
        Section existingSection = sections.stream()
                .filter(it -> it.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .orElse(null);
        if (existingSection != null) {
            int indexOfExisting = sections.indexOf(existingSection);
            existingSection.connectUpStationTo(section);
            sections.add(indexOfExisting, section);
        }
    }

    private void connectIfLast(Section section) {
        if (sections.contains(section)) {
            return;
        }

        Station currentLastStation = sections.get(sections.size() - 1).getDownStation();
        Station upStationOfInputSection = section.getUpStation();
        if (upStationOfInputSection.equals(currentLastStation)) {
            sections.add(section);
        }
    }

    private void connectIfFront(Section section) {
        if (sections.contains(section)) {
            return;
        }

        Station currentFrontMostStation = sections.get(0).getUpStation();
        Station downStationOfInputSection = section.getDownStation();
        if (downStationOfInputSection.equals(currentFrontMostStation)) {
            sections.add(0, section);
        }
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            addStation(stations, section);
        }
        return stations;
    }

    private List<Station> addStation(List<Station> stations, Section section) {
        if (!stations.contains(section.getUpStation())) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
            return stations;
        }

        stations.add(section.getDownStation());
        return stations;
    }

    public void removeStation(Station station) {
        validateBeforeRemove(station);
        Optional<Section> downStationMatchingSection = sections.stream()
                .filter(section -> section.isDownStation(station)).findFirst();
        Optional<Section> upStationMatchingSection = sections.stream()
                .filter(section -> section.isUpStation(station)).findFirst();
        //상행종점
        if (!downStationMatchingSection.isPresent() && upStationMatchingSection.isPresent()) {
            sections.remove(upStationMatchingSection.get());
            return;
        }
        //하행종점
        if (!upStationMatchingSection.isPresent() && downStationMatchingSection.isPresent()) {
            sections.remove(downStationMatchingSection.get());
            return;
        }
        disconnectMiddleSection(downStationMatchingSection.get(), upStationMatchingSection.get());
    }

    private void validateBeforeRemove(Station station) {
        if (sections.size() < MINIMUM_REMOVABLE_SIZE) {
            throw new CannotDeleteException("마지막 구간은 삭제할 수 없습니다.");
        }
    }

    //중간에 있는 지하철역의 경우에는 앞구간의 하행역을 수정하고, 뒷구간을 삭제처리한다
    private void disconnectMiddleSection(Section upSection, Section downSection) {
        upSection.disconnectDownStationFrom(downSection);
        sections.remove(downSection);
    }
}
