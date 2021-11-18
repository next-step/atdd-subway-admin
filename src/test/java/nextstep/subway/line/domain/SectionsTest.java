package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionsTest {
    @DisplayName("상행부터 하행까지 정렬되어 지하철역 목록을 반환한다")
    @Test
    void createStations() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(new Station(3L, "강남역"), new Station(4L, "사당역"), 10));
        sections.add(new Section(new Station(4L, "사당역"), new Station(5L, "합정역"), 10));
        sections.add(new Section(new Station(1L, "신당역"), new Station(2L, "삼성역"), 10));
        sections.add(new Section(new Station(2L, "삼성역"), new Station(3L, "강남역"), 10));
        sections.add(new Section(new Station(5L, "합정역"), new Station(6L, "신촌"), 10));

        // when
        List<Station> stations = sections.createStations();

        // then
        assertThat(stations).extracting("id")
            .containsExactly(1L, 2L, 3L, 4L, 5L, 6L);
    }
}
