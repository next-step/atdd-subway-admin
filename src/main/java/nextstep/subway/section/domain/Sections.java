package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval=true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Stream<Section> stream() {
        return sections.stream();
    }

    public void add(Section section) {
        if (this.isEmpty()) {
            sections.add(section);
            return;
        }
        if (this.contains(section)) {
            return;
        }

        section.positioningAt(sections);
    }

    public void delete(Station station) {
        if (sections.size() <= 1) {
            throw new IllegalStateException();
        }

        station.deleteFrom(sections);
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public void validateConnectionWith(Section sectionIn) {
        if (sections.isEmpty()) {
            return;
        }
        alreadyInBoth(sectionIn);
        nothingInBoth(sectionIn);
    }

    private void alreadyInBoth(Section sectionIn) {
        if (sectionIn.bothStationsAreAlreadyIn(stations())) {
            throw new IllegalArgumentException("둘 다 이미 들어있는 역.");
        }
    }

    private void nothingInBoth(Section sectionIn) {
        if (sectionIn.bothStationsAreNotIn(stations())) {
            throw new IllegalArgumentException("둘 다 들어있지 않은 역.");
        }
    }

    public List<Station> stations() {
        return OrderedSections.of(this.sections).get().stream()
                .flatMap(section -> section.upDownStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> get() {
        return sections;
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

}
