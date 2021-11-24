package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.DistanceTest.*;
import static nextstep.subway.line.domain.SectionTest.*;
import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    void 지하철_구간_목록으로_지하철_구간_일급컬렉션을_만들수_있다() {
        //given
        final Set<Section> expectedSections = new HashSet<>();
        expectedSections.add(지하철_구간_생성_및_검증(지하철역_생성_및_검증("강남"), 지하철역_생성_및_검증("삼성"), 지하철_노선_구간거리_생성_및_검증(7)));
        expectedSections.add(지하철_구간_생성_및_검증(지하철역_생성_및_검증("교대"), 지하철역_생성_및_검증("강남"), 지하철_노선_구간거리_생성_및_검증(3)));

        //when
        final Sections actualSections = 지하철_구간_일급컬렉션_생성(expectedSections);

        //then
        assertThat(actualSections.getSections()).containsAll(expectedSections);
    }

    public static Sections 지하철_구간_일급컬렉션_생성(final Set<Section> expectedSections) {
        return new Sections(expectedSections);
    }
}