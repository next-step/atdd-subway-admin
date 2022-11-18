package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {}

    private Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public int size() {
        return this.sections.size();
    }

    public void add(Section section) {
        validateAddContainsAllStation(section);
        validateAddNonContainsStation(section);
        updateSections(section);
        sections.add(section);
    }

    private void validateAddContainsAllStation(Section section) {
        List<Station> stations = getStations();
        if (!stations.isEmpty() && stations.containsAll(section.getStations())) {
            throw new IllegalArgumentException("이미 모든 역이 존재 합니다.");
        }
    }

    private void validateAddNonContainsStation(Section section) {
        List<Station> stations = getStations();
        if (!stations.isEmpty() && stations.stream().noneMatch(section.getStations()::contains)) {
            throw new IllegalArgumentException("등록하려는 구간의 상행역과 하행역 둘 중 하나라도 기존 구간의 역에 포함되지 않으면 구간을 등록할 수 없습니다.");
        }
    }

    private void updateSections(Section section) {
        sections.stream().forEach(s -> s.update(section));
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

}
