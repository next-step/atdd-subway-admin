package nextstep.subway.domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private final static int FIRST = 0;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    protected Sections() {
    }

    public Sections(final List<Section> values) {
        this.values = values;
    }

    public void add(Section section) {
        values.add(section);
    }

    public List<Station> getStations() {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Station> result = new HashSet<>();
        for (Section section : values) {
            result.add(section.getUpStation());
            result.add(section.getDownStation());
        }
        return Collections.unmodifiableList(new ArrayList<>(result));
    }

    public List<Station> getOrderedStations() {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        final Station first = getFirstStation();
        final List<Station> rest = getUpStationsOf(first);
        List<Station> result = new ArrayList<>();
        result.add(first);
        result.addAll(rest);
        return Collections.unmodifiableList(result);
    }

    private Station getFirstStation() {
        Station first = values.get(FIRST).getUpStation();
        while (hasDownSection(first)) {
            Section downSection = getDownSection(first);
            first = downSection.getUpStation();
        }
        return first;
    }

    private boolean hasDownSection(Station station) {
        return values.stream()
            .anyMatch(section -> section.equalDownStation(station));
    }

    private Section getDownSection(Station station) {
        return values.stream()
            .filter(section -> section.equalDownStation(station))
            .findAny()
            .orElseThrow(() ->
                new IllegalArgumentException("There is no down section of given station"));
    }

    private List<Station> getUpStationsOf(Station station) {
        List<Station> result = new ArrayList<>();
        Station current = station;
        while (hasUpSection(current)) {
            Section upSection = getUpSection(current);
            current = upSection.getUpStation();
            result.add(current);
        }
        return result;
    }

    private boolean hasUpSection(Station station) {
        return values.stream()
            .anyMatch(section -> section.equalUpStation(station));
    }

    private Section getUpSection(Station station) {
        return values.stream()
            .filter(section -> section.equalUpStation(station))
            .findAny()
            .orElseThrow(() ->
                new IllegalArgumentException("There is no up section of given station"));
    }
}
