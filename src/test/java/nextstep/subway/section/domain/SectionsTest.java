package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;
    private Sections sections;

    @BeforeEach
    void setUp() {
        stationA = new Station("A");
        stationB = new Station("B");
        stationC = new Station("C");
        stationD = new Station("D");
        stationE = new Station("E");
        sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(stationA, stationC, 10),
                new Section(stationA, stationD, 10),
                new Section(stationB, stationE, 10)
        )));
    }

    @DisplayName("상행역 부터 하행역 순으로 정렬되어야 한다.")
    @Test
    void getSortedStations() {
        //when
        List<Station> actual = sections.getSortedStations();

        //then
        assertAll(() -> {
            assertThat(actual.size()).isEqualTo(5);
            assertThat(actual.get(0)).isEqualTo(stationA);
            assertThat(actual.get(1)).isEqualTo(stationC);
            assertThat(actual.get(2)).isEqualTo(stationD);
            assertThat(actual.get(3)).isEqualTo(stationB);
            assertThat(actual.get(4)).isEqualTo(stationE);
        });
    }

    @Test
    void add() {
        //given
        Section section = new Section(stationB, stationD, 10);

        //when
        sections.add(section);

        //then
        assertThat(sections.getSections()).contains(section);
    }
}