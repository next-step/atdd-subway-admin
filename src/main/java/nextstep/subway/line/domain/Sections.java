package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> values;

    protected Sections() {
        this(new ArrayList<>());
    }

    protected Sections(List<Section> values) {
        this.values = copy(values);
    }

    private static List<Section> copy(List<Section> sections) {
        return sections.stream()
                .map(Section::from)
                .collect(Collectors.toList());
    }

    public List<Section> get() {
        return Collections.unmodifiableList(values);
    }

    public List<Station> getAllStation() {
        return values.stream()
                .map(Section::getStations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public Distance distance() {
        return values.stream()
                .map(Section::getDistance)
                .reduce(new Distance(), Distance::add);
    }

    public int size() {
        return values.size();
    }

    public void add(Section newSection) {
        if (Objects.isNull(newSection)) {
            return;
        }

        if (values.isEmpty()) {
            values.add(newSection);
            return;
        }

        validateStationsToAdd(newSection);

        updateExistingSection(newSection);
        values.add(newSection);
    }

    private void updateExistingSection(Section newSection) {
        values.stream()
                .filter(section -> section.hasSameUpOrDownStation(newSection))
                .findFirst()
                .ifPresent(section -> section.update(newSection));
    }

    private void validateStationsToAdd(Section newSection) {
        List<Station> existingStations = getAllStation();
        List<Station> stationsToAdd = newSection.getStations();
        boolean notFound = Stream.concat(existingStations.stream(), stationsToAdd.stream()).distinct().count()
                == existingStations.size() + stationsToAdd.size();

        if (notFound) {
            throw new IllegalArgumentException("기존 구간에 존재하지 않는 상하행역을 가진 구간을 추가할 수 없습니다.");
        }
    }
}
