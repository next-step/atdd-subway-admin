package nextstep.subway.section;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 단위테스트")
public class SectionUnitTest {
    @Test
    @DisplayName("하나의 역만 같은 경우인지 체크하는 로직 검증")
    void matchedOnlyOneStation() {

        Station 서울역 = new Station("서울역");
        Station 광명역 = new Station("광명역");
        Station 대전역 = new Station("대전역");
        Station 동대구역 = new Station("동대구역");
        Station 부산역 = new Station("부산역");

        Section 서울대전구간 = new Section(서울역, 대전역, 10);

        Section 서울광명구간 = new Section(서울역, 광명역, 2);
        Section 광명동대구역구간 = new Section(광명역, 동대구역, 2);
        Section 광명대전구간 = new Section(광명역, 대전역, 2);
        assertThat(서울대전구간.matchedOnlyOneStation(서울광명구간)).isTrue();
        assertThat(서울대전구간.matchedOnlyOneStation(광명동대구역구간)).isFalse();
        assertThat(서울대전구간.matchedOnlyOneStation(광명대전구간)).isTrue();
    }
}
