package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Section> sections = new ArrayList<>();

    public Sections() { }

    public Stream<Section> stream() {
        return sections.stream();
    }

    public void add(Section section) {
        if (this.contains(section)) {
            return;
        }
        sections.add(section);
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public boolean validateAbout(Section sectionIn) {
        alreadyInBoth(sectionIn);
        nothingInBoth(sectionIn);
        return true;
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
        return this.sections.stream()
                .flatMap(section -> section.upDownStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Station> orderedStations() {
        return OrderedSections.of(this.sections).get().stream()
                .flatMap(section -> section.upDownStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

     public OrderedSections orderedSections() {
        return OrderedSections.of(this.sections);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

}
