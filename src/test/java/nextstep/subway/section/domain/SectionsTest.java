package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    void 지하철_구간_목록으로_지하철_구간_일급컬렉션을_만들수_있다() {
        //given
        final Set<Section> expectedSections = new HashSet<>();
        expectedSections.add(new Section(new Station("강남"), new Station("삼성"), new Distance(7)));
        expectedSections.add(new Section(new Station("교대"), new Station("강남"), new Distance(3)));

        //when
        final Sections actualSections = new Sections(expectedSections);

        //then
        assertThat(actualSections.getSections()).containsAll(expectedSections);
    }
}