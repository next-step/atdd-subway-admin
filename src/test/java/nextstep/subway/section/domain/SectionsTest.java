package nextstep.subway.section.domain;

import nextstep.subway.exception.CannotRemoveSingleSectionException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @DisplayName("구간에 저장된 지하철역 정보들을 상행부터 하행순으로 중복을 제거하여 추출한다.")
    void extractStationsByRemoveDuplicateStation_test() {
        //given
        Station 판교역 = new Station("판교역");
        Station 야탑역 = new Station("이매역");
        Section 첫번째_구간 = new Section(판교역, 야탑역, 10);

        Station 모란역 = new Station("모란역");
        Section 두번째_구간 = new Section(모란역, 야탑역, 20);

        //when
        Sections sections = new Sections(List.of(첫번째_구간, 두번째_구간));

        //then
        assertThat(sections.extractStations()).containsExactly(
                판교역,
                모란역,
                야탑역
        );
    }

    @Test
    @DisplayName("단일 구간인 경우 제거 시 예외처리가 발생한다.")
    void validateSingleSection_test() {
        //given
        Station 판교역 = new Station("판교역");
        Station 야탑역 = new Station("이매역");
        Section 첫번째_구간 = new Section(판교역, 야탑역, 10);

        Sections sections = new Sections(List.of(첫번째_구간));

        //when
        assertThrows(CannotRemoveSingleSectionException.class,
                () -> sections.validateAndRemoveSectionByStation(판교역)
        );
    }

    @Test
    @DisplayName("시작 구간에 존재하는 역을 제거한다.")
    void removeStartEdgeStation_test() {
        //given
        Line 첫번째_라인 = new Line("1호선", "red");

        Station 판교역 = new Station(1L, "판교역");
        Station 이매역 = new Station(2L, "이매역");
        Section 첫번째_구간 = new Section(1L, 판교역, 이매역, 첫번째_라인, 10);

        Station 야탑역 = new Station(3L, "야탑역");
        Section 두번째_구간 = new Section(2L, 야탑역, 이매역, 첫번째_라인, 15);

        Sections sections = new Sections(Lists.newArrayList(첫번째_구간, 두번째_구간));

        //when
        sections.removeSectionByStation(판교역, 0);

        //then
        assertThat(sections.getSections()).containsExactly(두번째_구간);
    }

    @Test
    @DisplayName("종착역을 제거한다.")
    void removeEndEdgeStation_test() {
        //given
        Line 첫번째_라인 = new Line("1호선", "red");

        Station 판교역 = new Station(1L, "판교역");
        Station 야탑역 = new Station(3L, "야탑역");
        Station 서현역 = new Station(4L, "서현역");
        Station 이매역 = new Station(2L, "이매역");

        Section 첫번째_구간 = new Section(1L, 판교역, 이매역, 첫번째_라인, 10);
        Section 두번째_구간 = new Section(이매역, 야탑역, 15);
        Section 세번째_구간 = new Section(야탑역, 서현역, 5);

        Sections sections = new Sections(Lists.newArrayList(첫번째_구간, 두번째_구간, 세번째_구간));

        //when
        sections.removeSectionByStation(서현역, 2);

        //then
        assertThat(sections.getSections()).containsExactly(
                첫번째_구간,
                두번째_구간
        );
    }

    @Test
    @DisplayName("중간역을 제거한다.")
    void removeMiddleEdgeStation_test() {
        //given
        Line 첫번째_라인 = new Line("1호선", "red");

        Station 판교역 = new Station(1L, "판교역");
        Station 야탑역 = new Station(3L, "야탑역");
        Station 서현역 = new Station(4L, "서현역");
        Station 이매역 = new Station(2L, "이매역");

        Section 첫번째_구간 = new Section(1L, 판교역, 이매역, 첫번째_라인, 10);
        Section 두번째_구간 = new Section(2L, 이매역, 야탑역, 15);
        Section 세번째_구간 = new Section(3L, 야탑역, 서현역, 5);

        Sections sections = new Sections(Lists.newArrayList(첫번째_구간, 두번째_구간, 세번째_구간));

        //when
        sections.removeSectionByStation(야탑역, 1);

        //then
        Section createdMiddleSection = new Section(2L, 판교역, 야탑역, 25);
        assertThat(sections.getSections()).containsExactly(
                createdMiddleSection,
                세번째_구간
        );
    }
}
