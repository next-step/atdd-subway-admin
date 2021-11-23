package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {
    @DisplayName("상행선-하행선 구간 먼저 추가된 순으로 정렬된 역들을 가져온다")
    @Test
    void testGetStations() {
        // given
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 광명역 = new Station("광명역");
        Station 영등포역 = new Station("영등포역");
        Sections sections = new Sections();
        sections.add(new Section(new Line(), 강남역, 광교역, 100));
        sections.add(new Section(new Line(), 광명역, 영등포역, 100));

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).hasSize(4)
                .map(Station::getName)
                .containsExactly("강남역", "광교역", "광명역", "영등포역");
    }
}
