package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new LinkedList<>();

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        if (containsAll(section) || containsNothing(section)) {
            throw new IllegalArgumentException("적합하지 않은 구간입니다");
        }
        if (matchOutside(section)) {
            sections.add(section);
            return;
        }
        if (modifyBy(section)) {
            sections.add(section);
        }
    }

    private boolean containsAll(Section section) {
        return contains(section.getUpStation()) && contains(section.getDownStation());
    }

    private boolean containsNothing(Section section) {
        return !contains(section.getUpStation()) && !contains(section.getDownStation());
    }

    private boolean contains(Station station) {
        return sections.stream()
                .anyMatch(section -> section.contains(station));
    }

    private boolean matchInside(Section section) {
        return sections.stream().anyMatch(section::matchInside);
    }

    private boolean matchOutside(Section section) {
        return !matchInside(section) && sections.stream().anyMatch(section::matchOutside);
    }

    private boolean modifyBy(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.modifyBy(newSection));
    }

    public List<Station> stations() {
        return new SectionNavigator(sections).orderedStations();
    }
}
