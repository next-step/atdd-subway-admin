package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    protected static final String SECTION_CAN_NOT_BE_NULL = "section null일 수 없습니다.";
    protected static final String DUPLICATE_SECTION = "이미 등록된 section입니다.";

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        validate(section);
        sections.add(section);
    }

    private void validate(Section section) {
        if (section == null) {
            throw new IllegalArgumentException(SECTION_CAN_NOT_BE_NULL);
        }
        if (sections.contains(section)) {
            throw new IllegalArgumentException(DUPLICATE_SECTION);
        }
    }
}
