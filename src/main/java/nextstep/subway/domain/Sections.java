package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.dto.StationResponse;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<StationResponse> toInOrderStationResponse() {
        return getInOrderSection().stream()
                .map(Section::nowStation)
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public List<Section> getInOrderSection() {
        List<Section> inOrderSection = new ArrayList<>();
        Section section = lineFirstSection();

        while (section != null) {
            inOrderSection.add(section);
            section = findNextSection(section.nowStation());
        }
        return inOrderSection;
    }

    private Section lineFirstSection() {
        Optional<Section> firstSection = this.sections.stream()
                .filter(Section::isLineFirstSection)
                .findFirst();

        return firstSection.orElseThrow(() -> new IllegalStateException("해당 라인에 첫번째 섹션이 존재하지 않습니다."));
    }

    private Section findNextSection(Station station) {
        return this.sections.stream()
                .filter(it -> it.previousStation() == station)
                .findFirst()
                .orElse(null);
    }

    public int size() {
        return this.sections.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
