package nextstep.subway.domain;

import nextstep.subway.exception.DuplicatedSectionException;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line")
    private List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section section) {
        validateSection(section);
        sections.add(section);
    }

    private void validateSection(Section newSection) {
        sections.forEach(section -> {
            boolean isExists = section.isExistsSection(newSection);
            if (isExists) {
                throw new DuplicatedSectionException(newSection.getUpStation().getId(), newSection.getDownStation().getId());
            }
        });
    }
}
