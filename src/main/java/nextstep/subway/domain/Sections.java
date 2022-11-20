package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "line")
    private List<Section> content = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> content) {
        this.content = new ArrayList<>(content);
    }

    public void addSection(Section newSection) {
        validateNotContainsAny(newSection);
        validateAlreadyContainsAll(newSection);

        content.forEach(section -> section.modify(newSection));
        content.add(newSection);
    }

    public void removeSectionById(Long id) {
        content.removeIf(section -> section.getId().equals(id));
    }

    public List<Station> getStations() {
        return content.stream()
                .flatMap(section -> section.getStations().stream())
                .collect(toList());
    }

    private void validateNotContainsAny(Section section) {
        List<Station> stations = this.getStations();
        if (stations.isEmpty()) {
            return;
        }
        boolean hasNoneStation = section.getStations().stream().noneMatch(stations::contains);
        if (hasNoneStation) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않아 추가할 수 없습니다.");
        }
    }

    private void validateAlreadyContainsAll(Section section) {
        if (getStations().containsAll(section.getStations())) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있어 추가할 수 없습니다.");
        }
    }
}
