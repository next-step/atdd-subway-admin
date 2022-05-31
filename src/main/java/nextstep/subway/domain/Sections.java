package nextstep.subway.domain;

import nextstep.subway.util.StationsComparator;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Section> getOrderSections() {
        sections.sort(new StationsComparator());
        return sections;
    }

    public Optional<Section> getPrevSectionByStationId(Long stationId) {
        return sections.stream().filter(section -> section.getDownStation().getId() == stationId).findFirst();
    }

    public Optional<Section> getNextSectionByStationId(Long stationId) {
        return sections.stream().filter(section -> section.getUpStation().getId() == stationId).findFirst();
    }

    public int size() {
        return this.sections.size();
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public void removeSection(Optional<Section> section) {
        if (section.isPresent()) {
            sections.remove(section.get());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sections)) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(getSections(), sections1.getSections());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSections());
    }

    @Override
    public String toString() {
        return "Sections{" +
                '}';
    }
}
