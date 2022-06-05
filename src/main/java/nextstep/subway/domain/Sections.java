package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
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

    public void remove(Station station) {
        if (isLastSection()) {
            throw new IllegalArgumentException("마지막 구간은 삭제할 수 없습니다");
        }
        if (isMidStation(station)) {
            removeMid(station);
            return;
        }
        removeEnd(station);
    }

    private boolean isLastSection() {
        return sections.size() == 1;
    }

    private boolean isMidStation(Station station) {
        return sections.stream()
                .filter(section -> section.contains(station))
                .count() >= 2;
    }

    private void removeMid(Station station) {
        removeAll(station).stream()
                .reduce(Section::merge)
                .ifPresent(this.sections::add);
    }

    private void removeEnd(Station station) {
        removeAll(station);
    }

    private List<Section> removeAll(Station station) {
        List<Section> removeSections = sections.stream()
                .filter(section -> section.contains(station))
                .collect(Collectors.toList());
        removeSections.forEach(this.sections::remove);
        return removeSections;
    }
}
