package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {
    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;
    private Station stationF;
    private Sections sections;

    @BeforeEach
    void setUp() {
        stationA = new Station("A");
        ReflectionTestUtils.setField(stationA, "id", 1L);
        stationB = new Station("B");
        ReflectionTestUtils.setField(stationB, "id", 2L);
        stationC = new Station("C");
        ReflectionTestUtils.setField(stationC, "id", 3L);
        stationD = new Station("D");
        ReflectionTestUtils.setField(stationD, "id", 4L);
        stationE = new Station("E");
        ReflectionTestUtils.setField(stationE, "id", 5L);
        stationF = new Station("F");
        ReflectionTestUtils.setField(stationF, "id", 6L);
        sections = new Sections(new ArrayList<>(Arrays.asList(
                Section.of(stationC, stationD, 10),
                Section.of(stationB, stationC, 10),
                Section.of(stationA, stationB, 10),
                Section.of(stationD, stationE, 10)
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
            assertThat(actual.get(1)).isEqualTo(stationB);
            assertThat(actual.get(2)).isEqualTo(stationC);
            assertThat(actual.get(3)).isEqualTo(stationD);
            assertThat(actual.get(4)).isEqualTo(stationE);
        });
    }

    @DisplayName("상행역과 하행역을 노선 구간에 추가한다.")
    @Test
    void add() {
        //given
        Section section = Section.of(stationE, stationF, 1);

        //when
        sections.add(section);

        //then
        assertThat(sections.getSections()).contains(section);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.")
    @Test
    void add_exists_exception() {
        //given
        Section section = Section.of(stationA, stationB, 10);

        //when
        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class) //then
                .hasMessage(Sections.EXISTS_SECTION_EXCEPTION_MESSAGE);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.")
    @Test
    void add_not_exists_exception() {
        //given
        Section section = Section.of(new Station("new1"), new Station("new2"), 10);

        //when
        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class) //then
                .hasMessage(Sections.NOT_EXISTS_ALL_STATIONS_EXCEPTION_MESSAGE);
    }
}