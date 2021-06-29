package nextstep.subway.section.domain;

import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidateDistanceException;
import nextstep.subway.exception.NotContainSectionException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {

    private Station 판교역;
    private Station 이매역;
    private Line 첫번째_라인;
    private Section 첫번째_구간;

    @BeforeEach
    void setUp() {
        판교역 = new Station(1L, "판교역");
        이매역 = new Station(2L, "이매역");
        첫번째_라인 = new Line("1호선", "red");
        첫번째_구간 = new Section(1L, 판교역, 이매역, 첫번째_라인, 10);
    }

    @Test
    @DisplayName("기존 상행과 요청한 상행이 같은 경우 구간을 추가한다.")
    void addSectionWhenSameUpStation_test() {
        //given
        ArrayList<Section> sections = new ArrayList<>();

        //when
        첫번째_구간.validateSectionAndAddSection(5, new Station(1L, "판교역"), new Station(3L, "야탑역"), sections);

        //then
        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("기존 상행과 요청한 하행이 같은 경우 구간을 추가한다.")
    void addSectionWhenSameAnotherPosition_test() {
        //given
        ArrayList<Section> sections = new ArrayList<>();

        //when
        첫번째_구간.validateSectionAndAddSection(5, new Station(3L, "야탑역"), new Station(1L, "판교역"), sections);

        //then
        assertThat(sections).hasSize(1);
    }

    @Test
    @DisplayName("요청한 구간과 기존의 구간의 상행과 하행이 모두 같은 경우 예외처리한다.")
    void validateDuplicateSection_test() {
        //given
        ArrayList<Section> sections = new ArrayList<>();

        //when
        assertThrows(DuplicateSectionException.class,
                () -> 첫번째_구간.validateSectionAndAddSection(5, new Station(1L, "판교역"), new Station(2L, "이매역"), sections)
        );
    }

    @Test
    @DisplayName("요청한 구간과 기존의 구간의 상행과 하행이 모두 같은 경우 예외처리한다.")
    void validateSectionDistance_test() {
        //given
        ArrayList<Section> sections = new ArrayList<>();
        Station 야탑역 = new Station(2L, "야탑역");
        첫번째_구간 = new Section(판교역, 야탑역, 10);

        //when
        assertThrows(InvalidateDistanceException.class,
                () -> 첫번째_구간.validateSectionAndAddSection(15, new Station(1L, "판교역"), new Station(3L, "이매역"), sections)
        );
    }

    @Test
    @DisplayName("요청한 구간의 역들이 기존 구간의 역들에 포함되지 않는 경우 예외처리한다.")
    void validateAnyContainSection_test() {
        //given
        ArrayList<Section> sections = new ArrayList<>();
        Station 야탑역 = new Station(2L, "야탑역");
        첫번째_구간 = new Section(판교역, 야탑역, 10);

        //when
        assertThrows(NotContainSectionException.class,
                () -> 첫번째_구간.validateSectionAndAddSection(15, new Station(3L, "이매역"), new Station(4L, "수내역"), sections)
        );
    }

    @Test
    @DisplayName("시작 구간에 존재하는 역을 제거한다.")
    void removeStartEdgeStation_test() {
        //given
        Station 야탑역 = new Station(3L, "야탑역");
        Station 서현역 = new Station(4L, "서현역");

        Section 두번째구간 = new Section(야탑역, 이매역, 15);

        ArrayList<Section> sections = new ArrayList<>();
        sections.add(첫번째_구간);
        sections.add(두번째구간);
        sections.add(new Section(이매역, 서현역, 5));

        //when
        첫번째_구간.removeSectionByStation(sections, 판교역, 0);

        //then
        assertThat(sections.get(0)).isEqualTo(두번째구간);
    }

    @Test
    @DisplayName("종착역을 제거한다.")
    void removeEndEdgeStation_test() {
        //given
        Station 야탑역 = new Station(3L, "야탑역");
        Station 서현역 = new Station(4L, "서현역");

        Section 두번째구간 = new Section(이매역, 야탑역, 15);
        Section 세번째구간 = new Section(야탑역, 서현역, 5);

        ArrayList<Section> sections = new ArrayList<>();
        sections.add(첫번째_구간);
        sections.add(두번째구간);
        sections.add(세번째구간);

        //when
        첫번째_구간.removeSectionByStation(sections, 서현역, 2);

        //then
        assertThat(sections.get(1)).isEqualTo(두번째구간);
    }

    @Test
    @DisplayName("중간역을 제거한다.")
    void removeMiddleEdgeStation_test() {
        //given
        Station 야탑역 = new Station(3L, "야탑역");
        Station 서현역 = new Station(4L, "서현역");

        Section 두번째구간 = new Section(2L, 이매역, 야탑역, 15);
        Section 세번째구간 = new Section(3L, 야탑역, 서현역, 5);

        ArrayList<Section> sections = new ArrayList<>();
        sections.add(첫번째_구간);
        sections.add(두번째구간);
        sections.add(세번째구간);

        //when
        두번째구간.removeSectionByStation(sections, 야탑역, 1);

        //then
        Section createdMiddleSection = new Section(2L, 판교역, 야탑역, 25);
        assertThat(sections.get(0)).isEqualTo(createdMiddleSection);
    }
}
