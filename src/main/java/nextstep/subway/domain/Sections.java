package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            validate(section);
            updateSection(section);
        }
        sections.add(section);
    }

    public Set<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toSet());
    }

    private void validate(Section section) {
        Set<Station> stations = getStations();
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("구간이 이미 등록되어 있습니다.");
        }
        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나라도 포함되어있어야 합니다.");
        }
    }

    private void updateSection(Section newSection) {
        sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateByUpSection(newSection));

        sections.stream()
                .filter(it -> it.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateByDownSection(newSection));
    }
}
