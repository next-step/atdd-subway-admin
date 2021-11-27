package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionsTest {
    @Test
    void 구간에서_역_목록을_가져온다() {
        Sections sections = new Sections(
            Arrays.asList(new Section(new Station("강남역"), new Station("양재역"), new Distance(10))));
        List<Station> stations = sections.getStations();

        assertThat(stations).isEqualTo(Arrays.asList(new Station("강남역"), new Station("양재역")));
    }
}