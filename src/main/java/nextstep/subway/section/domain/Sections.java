package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lineId")
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        return sections.stream()
            .flatMap(section -> section.getStations().stream())
            .distinct()
            .collect(Collectors.toList());
    }

    public void add(Section newSection) {
        validateAlreadyContainsAll(newSection);
        validateNotContainsAny(newSection);
        sections.stream()
            .filter(section -> section.getUpStation().equals(newSection.getUpStation())
                || section.getDownStation().equals(newSection.getDownStation()))
            .findFirst()
            .ifPresent(section -> section.relocate(newSection));
        sections.add(newSection);
    }

    private void validateNotContainsAny(Section section) {
        List<Station> stations = this.getStations();
        if (stations.isEmpty()) {
            return;
        }
        if (section.getStations().stream().noneMatch(stations::contains)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    private void validateAlreadyContainsAll(Section section) {
        if (getStations().containsAll(section.getStations())) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }
}
