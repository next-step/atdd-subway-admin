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

    public void add(Section sectionIn) {
        if (this.isEmpty()) {
            sections.add(sectionIn);
            return;
        }
        if (this.contains(sectionIn)) {
            return;
        }

        sectionIn.positioningAt(sections);
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

    public List<Station> orderedStations() {
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

    public void delete(Station station) {
        List<Section> sections = this.sections;
        for (int i = 0; i < sections.size(); ++i) {
            Section section = sections.get(i);

            if (section.getUpStation().getName().equals(station.getName())) {
                if (i > 0 && i < sections.size() - 1) {
                    sections.get(i-1).setDownStation(sections.get(i+1).getUpStation());
                }
                sections.remove(i);
                break;
            }

            if (section.getDownStation().getName().equals(station.getName())) {
                if (i == 0 && i < sections.size() - 1) {
                    sections.get(i+1).setUpStation(sections.get(i).getUpStation());
                }
                if (i > 0 && i < sections.size() - 1) {
                    sections.get(i-1).setDownStation(sections.get(i+1).getUpStation());
                }
                sections.remove(i);
                break;
            }
        }
        this.sections = sections;
    }
}
