package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

class SectionsTest {
    private static final int DISTANCE = 10;

    private Station firstStation;
    private Station secondStation;
    private Station thirdStation;
    private Station forthStation;
    private Distance distance;
    private Section firstSection;
    private Section secondSection;
    private Section thirdSection;

    @BeforeEach
    void setUp() {
        firstStation = Station.from(1L, "판교역");
        secondStation = Station.from(2L, "정자역");
        thirdStation = Station.from(3L, "미금역");
        forthStation = Station.from(4L, "동천역");
        distance = Distance.from(DISTANCE);
        firstSection = Section.of(firstStation, secondStation, distance);
        secondSection = Section.of(secondStation, thirdStation, distance);
        thirdSection = Section.of(thirdStation, forthStation, distance);
    }

    @DisplayName("Sections 을 Section 목록으로 생성한다.")
    @Test
    void create() {
        // when & then
        assertThatNoException().isThrownBy(
            () -> Sections.from(Arrays.asList(firstSection, secondSection, thirdSection)));
    }

    @DisplayName("상행에서 하행순으로 지하철 역 목록을 반환한다.")
    @Test
    void sortStations() {
        // given
        Sections sections = Sections.from(Arrays.asList(secondSection, thirdSection, firstSection));

        // when
        List<Station> stations = sections.getSortedStations();

        // then
        assertEquals(stations, Arrays.asList(firstStation, secondStation, thirdStation, forthStation));
    }
}