package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> elements;

    protected Sections() {
        this.elements = new ArrayList<>();
    }

    public static Sections empty() {
        return new Sections();
    }

    public void add(Section section) {
        if (elements.size() > 0) {
            validate(section);
        }
        updateSection(section);
        elements.add(section);
    }

    private void validate(Section section) {
        containsAllStations(section);
        notContainsStations(section);
    }

    private void containsAllStations(Section newSection) {
        if(allStations().containsAll(newSection.allStations())) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
        }
    }

    private void notContainsStations(Section newSection) {
        Set<Station> stations = allStations();
        if(!stations.contains(newSection.getUpStation()) && !stations.contains(newSection.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    private void updateSection(Section newSection) {
        elements.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpStation(newSection));

        elements.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateDownStation(newSection));
    }

    private Set<Station> allStations() {
        return elements.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toSet());
    }

    public List<Section> get() {
        return elements;
    }

    @Override
    public String toString() {
        return "Sections{" +
                "elements=" + elements +
                '}';
    }
}
