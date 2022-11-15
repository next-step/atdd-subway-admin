package nextstep.subway.domain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Section> sections;

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = new LinkedList<>(sections);
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> assignedStations() {
        return this.sections.stream()
                .map(Section::stations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void add(Section newSection) {
        validateDuplicateUpDownStation(newSection);
        sections.forEach(section -> section.reorganize(newSection));
        sections.add(newSection);
    }

    public void validateDuplicateUpDownStation(Section newSection) {
        boolean isSame = this.sections.stream().anyMatch(section -> section.isSameUpDownStation(newSection));
        if (isSame) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.");
        }
    }
}
