package nextstep.subway.line.domain.stationAdapter;

import nextstep.subway.line.domain.Section;

public interface SectionCalculator {
    Section calculate(Section originalSection, Section addSection);
}
