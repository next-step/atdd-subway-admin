package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval=true)
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

    public void add(int index, Section section) {
        if (this.contains(section)) {
            return;
        }
        sections.add(index, section);
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
        if (stations().contains(sectionIn.getUpStation())
                && stations().contains(sectionIn.getDownStation())) {
            throw new IllegalArgumentException("둘 다 이미 들어있는 역.");
        }
    }

    private void nothingInBoth(Section sectionIn) {
        if (!stations().contains(sectionIn.getUpStation())
                && !stations().contains(sectionIn.getDownStation())) {
            throw new IllegalArgumentException("둘 다 들어있지 않은 역.");
        }
    }

    public List<Station> stations() {
        return this.sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Station> orderedStations() {
        return OrderedSections.of(this.sections).get().stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

     public OrderedSections orderedSections() {
        return OrderedSections.of(this.sections);
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
