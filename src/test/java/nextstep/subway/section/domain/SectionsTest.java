package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionsTest {

    @DisplayName("구간에서 역 목록을 가져온다.")
    @Test
    void getStations() {
        Sections sections = new Sections(
            Arrays.asList(new Section(new Station("강남역"), new Station("양재역"), new Distance(10))));
        List<Station> stations = sections.getStations();

        assertThat(stations).isEqualTo(Arrays.asList(new Station("강남역"), new Station("양재역")));
    }
}