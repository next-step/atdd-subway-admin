package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    @DisplayName("구간에 저장된 지하철역 정보들을 상행부터 하행순으로 추출한다.")
    void extractStations_test() {
        //given
        Station 판교역 = new Station("판교역");
        Station 이매역 = new Station("이매역");
        Section 첫번째_구간 = new Section(판교역, 이매역, 10);

        Station 모란역 = new Station("모란역");
        Station 야탑역 = new Station("야탑역");
        Section 두번째_구간 = new Section(모란역, 야탑역, 20);

        //when
        Sections sections = new Sections(List.of(첫번째_구간, 두번째_구간));

        //then
        assertThat(sections.extractStations()).containsExactly(
                판교역,
                모란역,
                이매역,
                야탑역
        );
    }
}
