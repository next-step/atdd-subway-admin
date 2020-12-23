package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.sections.Section;

public interface AddSectionPolicy {
    boolean addSection(final Section newSection);
}
