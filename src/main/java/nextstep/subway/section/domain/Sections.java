package nextstep.subway.section.domain;

import java.util.List;

public class Sections {

    public static final String AT_LEAST_ONE_SECTION_IS_REQUIRED = "1개 이상의 구간이 입력되어야 합니다.";
    private List<Section> sections;

    public Sections(List<Section> sections) {
        if (sections == null && sections.size() == 0) {
            throw new IllegalArgumentException(AT_LEAST_ONE_SECTION_IS_REQUIRED);
        }
        this.sections = sections;
    }

    public Section findFirstSection() {
        return sections.get(0);
    }

    public Section findLastSection() {
        return sections.get(sections.size() - 1);
    }
}
