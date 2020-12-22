package nextstep.subway.line.domain;

import nextstep.subway.line.domain.Section;

public interface SectionCalculator {
    void calculate(Section originalSection, Section addSection);
}
