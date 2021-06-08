package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.apache.commons.lang3.NotImplementedException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (section == null) {
            throw new IllegalArgumentException("Section is null.");
        }
        if (!this.contains(section)) {
            sections.add(section);
        }
    }

    public List<Section> get() {
        return sections;
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public List<Station> getOrderedStations() {
        throw new NotImplementedException();
    }

    public List<Section> getOrderedSections() {
        Section topSection = findTopSection();

        List<Section> orderedSections = makeOrderedSectionsFrom(topSection);

        return orderedSections;
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

    protected List<Section> makeOrderedSectionsFrom(Section topSection) {
        List<Section> orderedSections = new ArrayList<>();
        orderedSections.add(topSection);

        Map<String, Section> upMap = new HashMap<>();
        Map<String, Section> downMap = new HashMap<>();

        for (Section it : this.sections) {
            upMap.put(it.getUpStation().getName(), it);
            downMap.put(it.getDownStation().getName(), it);
        }

        Section prevSection = null;
        while (topSection != null) {
            prevSection = topSection;

            topSection = upMap.get(topSection.getDownStation().getName());

            if (topSection != null) {
                orderedSections.add(topSection);
            }
        }

        orderedSections.add(prevSection);

        return orderedSections;
    }
}
