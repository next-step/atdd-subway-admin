package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections;

    public Sections() {
        sections = new LinkedList<>();
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void updateStation(Section section) {
        changeUpStation(section);
        changeDownStation(section);
        sections.add(section);
    }

    private void changeDownStation(Section section) {
        sections.stream()
                .filter(v -> v.getDownStation().equals(section.getDownStation()))
                .findFirst()
                .ifPresent(v -> v.changeDownStation(section));
    }

    private void changeUpStation(Section section) {
        sections.stream()
                .filter(v -> v.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(v -> v.changeUpStation(section));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        LinkedList<Section> result = new LinkedList<>();
        Deque<Section> queue = new LinkedList<>(sections);
        result.add(queue.pop());

        while (!queue.isEmpty()) {
            Section pop = queue.pop();

            result.stream()
                    .map(Section::getUpStation)
                    .filter(v -> Objects.nonNull(pop.getDownStation()) && v.equals(pop.getDownStation()))
                    .findFirst()
                    .ifPresent(
                            v -> result.addFirst(pop)
                    );
            result.stream()
                    .map(Section::getDownStation)
                    .filter(v -> Objects.nonNull(pop.getUpStation()) && v.equals(pop.getUpStation()))
                    .findFirst()
                    .ifPresent(
                            v -> result.addLast(pop)
                    );
        }
        return collectStations(result);
    }

    private List<Station> collectStations(LinkedList<Section> result) {
        return result.stream().
                flatMap(Section::getProcessStations)
                .distinct()
                .collect(toList());
    }

    public int getTotalLength() {
        return this.sections.stream()
                .map(Section::getDistance)
                .reduce(0, Integer::sum);
    }

}
