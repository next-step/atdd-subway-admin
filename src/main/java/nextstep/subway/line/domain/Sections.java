package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

    public static Sections from(Sections sections) {
        return new Sections(sections.values);
    }

    private static List<Section> copy(List<Section> sections) {
        return sections.stream().map(Section::from).collect(Collectors.toList());
    }

    public List<Section> get() {
        return Collections.unmodifiableList(values);
    }

    public List<Station> getAllStation() {
        return values.stream().map(Section::getStations).flatMap(List::stream).distinct().collect(Collectors.toList());
    }

    public LineDistance distance() {
        return values.stream().map(Section::getDistance).reduce(new LineDistance(), LineDistance::add);
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

        updateExistingSection(newSection);
        values.add(newSection);
    }

    private void updateExistingSection(Section newSection) {
        values.stream()
                .filter(section -> section.hasSameUpOrDownStation(newSection))
                .findFirst()
                .ifPresent(section -> section.update(newSection));
    }
}
