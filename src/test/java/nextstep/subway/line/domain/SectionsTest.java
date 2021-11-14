package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("구간 목록 테스트")
class SectionsTest {

    @Test
    @DisplayName("지하철 구간 순서에 맞는 지하철역 목록을 반환한다.")
    void toStations() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 사당역 = new Station("사당역");
        Section section1 = new Section(강남역, 역삼역, new Distance(10));
        Section section2 = new Section(역삼역, 사당역, new Distance(15));

        Sections sections = new Sections();
        sections.add(section1);
        sections.add(section2);

        // when
        List<Station> stations = sections.toStations();

        // then
        assertThat(stations).containsExactly(강남역, 역삼역, 사당역);
    }
}
