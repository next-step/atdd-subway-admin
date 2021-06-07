package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections implements Iterable<Section> {

    private static final int FIRST_INDEX = 0;

    @OneToMany(mappedBy = "line")
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
        add(selectAddIndex(element), element);
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

    private int selectAddIndex(Section element) {
        if (getFirstStation().equals(element.getUpStation())) {
            return FIRST_INDEX;
        }
        if (getLastStation().equals(element.getDownStation())) {
            return lastIndex();
        }
        if (getUpStations().contains(element.getDownStation())) {
            return getUpStations().indexOf(element.getDownStation()) + 1;
        }
        return getDownStations().indexOf(element.getUpStation());
    }

    private void add(int index, Section element) {
        sections.add(index, element);
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }
}
