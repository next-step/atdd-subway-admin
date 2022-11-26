package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    //@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(Section section) {
        List<Section> sections = new ArrayList<>();
        sections.add(section);
        return new Sections(sections);
    }

    public void addSection(Section newSection) {
        // 상행 하행 둘 중 하나도 포함되어있지 않는 경우
        // 상행 하행 모두 기등록된 경우
        // Distance 가 같은 경우


        Optional<Section> upStation = sections.stream().filter(section -> section.isEqualUpStation(newSection)).findFirst();
        Optional<Section> downStation = sections.stream().filter(section -> section.isEqualDownStation(newSection)).findFirst();
        upStation.ifPresent(section -> section.updateUpStation(newSection));
        downStation.ifPresent(section -> section.updateDownStation(newSection));

        sections.add(newSection);
    }

    public List<Station> getStations() {
        return sections.stream().map(section -> section.getStations()).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }
}
