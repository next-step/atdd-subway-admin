package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.NotExistSectionAddPolicy;

import java.util.Arrays;

public enum SectionAddPolicy {
    ADD_WITH_UP_STATION(true, false),
    ADD_WITH_DOWN_STATION(false, true);

    private final boolean isUpStationSame;
    private final boolean isDownStationSame;

    SectionAddPolicy(final boolean isUpStationSame, final boolean isDownStationSame) {
        this.isUpStationSame = isUpStationSame;
        this.isDownStationSame = isDownStationSame;
    }

    public static SectionAddPolicy find(final boolean isUpStationSame, final boolean isDownStationSame) {
        return Arrays.stream(SectionAddPolicy.values())
                .filter(it -> it.isUpStationSame == isUpStationSame && it.isDownStationSame == isDownStationSame)
                .findFirst()
                .orElseThrow(() -> new NotExistSectionAddPolicy("Section을 추가할 수 없는 경우입니다."));
    }
}
