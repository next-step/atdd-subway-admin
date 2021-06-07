package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections implements Iterable<Section> {

    private static final int FIRST_INDEX = 0;
    private static final int FRONT_OF_SECTIONS = -1;

    @OneToMany(mappedBy = "line")
    @OrderBy("sequence ASC")
    private final List<Section> sections = new LinkedList<>();

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(getFirstStation());
        stations.addAll(getUpStations());
        return stations;
    }

    public Station getFirstStation() {
        return sections.get(FIRST_INDEX).getDownStation();
    }

    public Station getLastStation() {
        return sections.get(lastIndex()).getUpStation();
    }

    private int lastIndex() {
        return sections.size() - 1;
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

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public void add(Section element) {
        if (sections.isEmpty()) {
            sections.add(element);
            return;
        }
        validateAddable(element);
        add(selectDivisionIndex(element), element);
        synchronizeSequence();
    }

    private void validateAddable(Section section) {
        if (isStationAllContains(section)) {
            throw new IllegalArgumentException("구간에 속한 모든 역이 노선에 포함되어 있습니다. 역 정보를 확인해주세요.");
        }
        if (isStationNotContains(section)) {
            throw new IllegalArgumentException("구간에 속한 모든 역이 노선에 포함되어 있지 않습니다. 역 정보를 확인해주세요.");
        }
    }

    private boolean isStationAllContains(Section section) {
        return getStations().contains(section.getUpStation()) && getStations().contains(section.getDownStation());
    }

    private boolean isStationNotContains(Section section) {
        return !getStations().contains(section.getUpStation()) && !getStations().contains(section.getDownStation());
    }

    private int selectDivisionIndex(Section element) {
        if (getFirstStation().equals(element.getUpStation())) {
            return FRONT_OF_SECTIONS;
        }
        if (getLastStation().equals(element.getUpStation())) {
            return lastIndex();
        }
        if (getLastStation().equals(element.getDownStation())) {
            return sections.size();
        }
        if (getUpStations().contains(element.getDownStation())) {
            return getUpStations().indexOf(element.getDownStation()) + 1;
        }
        return getDownStations().indexOf(element.getUpStation()) - 1;
    }

    private void add(int index, Section element) {
        if (isEdge(index)) {
            addEdge(index, element);
            return;
        }
        divideSection(index, element);
    }

    private boolean isEdge(int index) {
        return index == FRONT_OF_SECTIONS || index == sections.size();
    }

    private void addEdge(int index, Section element) {
        if (index == FRONT_OF_SECTIONS) {
            sections.add(FIRST_INDEX, element);
            return;
        }
        sections.add(index, element);
    }

    private void divideSection(int index, Section element) {
        Section divisionTarget = sections.get(index);
        divisionTarget.divideDistance(element);
        if (divisionTarget.getUpStation().equals(element.getUpStation())) {
            divisionTarget.modifyUpStation(element.getDownStation());
            sections.add(index + 1, element);
        }
        if (divisionTarget.getDownStation().equals(element.getDownStation())) {
            divisionTarget.modifyDownStation(element.getUpStation());
            sections.add(index, element);
        }
    }

    private void synchronizeSequence() {
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            section.modifySequence(i);
        }
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }
}
