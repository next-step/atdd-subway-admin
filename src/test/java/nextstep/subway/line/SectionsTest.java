package nextstep.subway.line;

import nextstep.subway.line.domain.Sections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.SectionTest.논현역_신논현역_구간;
import static nextstep.subway.line.SectionTest.신논현역_강남역_구간;
import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    public static final Sections 구간들;

    static {
        Sections sections = new Sections();
        sections.add(SectionTest.신논현역_강남역_구간);
        구간들 = sections;
    }

    @DisplayName("구간역 목록을 조회한다.")
    @Test
    void name() {
        Sections sections = new Sections();
        sections.add(논현역_신논현역_구간);
        sections.add(신논현역_강남역_구간);
        assertThat(sections.getStations()).containsExactly(논현역, 신논현역, 강남역);
    }
}
