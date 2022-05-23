package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section newSection) {
        try {
            validateSection(newSection);
            sections.add(newSection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void validateSection(Section newSection) throws Exception {
        Optional<Section> validatedSection = sections.stream()
                .filter(section -> section.validateAddSection(newSection)).findAny();
        if (!validatedSection.isPresent()) {
            throw new Exception("구간을 등록할 수 없습니다.");
        }
    }
}
