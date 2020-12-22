package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.NotExistSectionAddPolicy;
import nextstep.subway.line.domain.stationAdapter.SectionCalculator;

import java.util.Arrays;

public enum AddSectionPolicy {
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

    AddSectionPolicy(
            final boolean isSameUpStation, final boolean isSameDownStation, final SectionCalculator sectionCalculator
    ) {
        this.isSameUpStation = isSameUpStation;
        this.isSameDownStation = isSameDownStation;
        this.sectionCalculator = sectionCalculator;
    }

    public static AddSectionPolicy find(final Section originalSection, final Section newSection) {
        return Arrays.stream(AddSectionPolicy.values())
                .filter(it -> it.isSameUpStation == originalSection.isSameUpStation(newSection) &&
                        it.isSameDownStation == originalSection.isSameDownStation(newSection))
                .findFirst()
                .orElseThrow(() -> new NotExistSectionAddPolicy("신규 Section으로 추가할 수 없는 대상입니다."));
    }

    public void calculateOriginalSection(Section originalSection, Section newSection) {
        this.sectionCalculator.calculate(originalSection, newSection);
    }
}
