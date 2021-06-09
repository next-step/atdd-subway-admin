package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void add(int index, Section section) {
        if (this.contains(section)) {
            return;
        }
        sections.add(index, section);
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public boolean checkIfValid(Section sectionIn) {
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
        return makeOrderedSectionsFromTop(findTopSection()).stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

     public OrderedSections orderedSections() {
        return OrderedSections.of(makeOrderedSectionsFromTop(findTopSection()));
//        return sections;
    }

    public Section findTopSection() {
        return sections.stream()
                .filter(section -> isTop(section))
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    private boolean isTop(Section section) {
        return !sections.stream()
                .filter(it -> it.getDownStation().getName()
                        .equals(section.getUpStation().getName()))
                .findAny()
                .isPresent();
    }

    private List<Section> makeOrderedSectionsFromTop(Section topSection) {
        List<Section> orderedSections = new ArrayList<>();
        orderedSections.add(topSection);

        Map<String, Section> stationMap = makeStationMap(sections);

        while (topSection != null) {
            topSection = stationMap.get(topSection.getDownStation().getName());
            addNextSection(orderedSections, topSection);
        }

        return orderedSections;
    }

    private Map<String, Section> makeStationMap(List<Section> sections) {
        Map<String, Section> sectionMap = new HashMap<>();
        for (Section it : this.sections) {
            sectionMap.put(it.getUpStation().getName(), it);
        }
        return sectionMap;
    }

    private void addNextSection(List<Section> orderedSections, Section section) {
        if (section == null) {
            return;
        }
        if (orderedSections.contains(section)) {
            return;
        }
        orderedSections.add(section);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

}
