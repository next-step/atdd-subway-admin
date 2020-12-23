package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.NotExistSectionAddPolicy;

import java.util.Arrays;

public enum OriginalSectionCalculator {
    ADD_WITH_UP_STATION(
            true,
            false,
            Section::updateUpStation
    ),
    ADD_WITH_DOWN_STATION(
            false,
            true,
            Section::updateDownStation
    );

    private final boolean isSameUpStation;
    private final boolean isSameDownStation;
    private final SectionCalculator sectionCalculator;

    OriginalSectionCalculator(
            final boolean isSameUpStation, final boolean isSameDownStation, final SectionCalculator sectionCalculator
    ) {
        this.isSameUpStation = isSameUpStation;
        this.isSameDownStation = isSameDownStation;
        this.sectionCalculator = sectionCalculator;
    }

    public static OriginalSectionCalculator find(final Section originalSection, final Section newSection) {
        return Arrays.stream(OriginalSectionCalculator.values())
                .filter(it -> it.isSameUpStation == originalSection.isSameUpStation(newSection) &&
                        it.isSameDownStation == originalSection.isSameDownStation(newSection))
                .findFirst()
                .orElseThrow(() -> new NotExistSectionAddPolicy("신규 Section으로 추가할 수 없는 대상입니다."));
    }

    public void calculate(Section originalSection, Section newSection) {
        this.sectionCalculator.calculate(originalSection, newSection);
    }
}
