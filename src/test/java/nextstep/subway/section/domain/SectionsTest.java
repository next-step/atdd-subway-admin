package nextstep.subway.section.domain;

import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidateDistanceException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
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
    @DisplayName("기존 상행과 요청한 상행이 같은 경우 구간을 추가한다.")
    void addSectionWhenSameUpStation_test() {
        //given
        ArrayList<Section> sections = new ArrayList<>();
        Station 판교역 = new Station(1L, "판교역");
        Station 야탑역 = new Station(2L, "이매역");
        Section 첫번째구간 = new Section(판교역, 야탑역, 10);

        //when
        첫번째구간.validateSectionAndAddSection(5, new Station(1L, "판교역"), new Station(3L, "야탑역"), sections);

        //then
        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("기존 상행과 요청한 하행이 같은 경우 구간을 추가한다.")
    void addSectionWhenSameAnotherPosition_test() {
        //given
        ArrayList<Section> sections = new ArrayList<>();
        Station 판교역 = new Station(1L, "판교역");
        Station 야탑역 = new Station(2L, "이매역");
        Section 첫번째구간 = new Section(판교역, 야탑역, 10);

        //when
        첫번째구간.validateSectionAndAddSection(5, new Station(3L, "야탑역"), new Station(1L, "판교역"), sections);

        //then
        assertThat(sections).hasSize(1);
    }

    @Test
    @DisplayName("요청한 구간과 기존의 구간의 상행과 하행이 모두 같은 경우 예외처리한다.")
    void validateDuplicateSection_test() {
        //given
        ArrayList<Section> sections = new ArrayList<>();
        Station 판교역 = new Station(1L, "판교역");
        Station 야탑역 = new Station(2L, "이매역");
        Section 첫번째구간 = new Section(판교역, 야탑역, 10);

        //when
        assertThrows(DuplicateSectionException.class,
                () -> 첫번째구간.validateSectionAndAddSection(5, new Station(1L, "판교역"), new Station(2L, "이매역"), sections)
        );
    }

    @Test
    @DisplayName("요청한 구간과 기존의 구간의 상행과 하행이 모두 같은 경우 예외처리한다.")
    void validateSectionDistance_test() {
        //given
        ArrayList<Section> sections = new ArrayList<>();
        Station 판교역 = new Station(1L, "판교역");
        Station 야탑역 = new Station(2L, "야탑역");
        Section 첫번째구간 = new Section(판교역, 야탑역, 10);

        //when
        assertThrows(InvalidateDistanceException.class,
                () -> 첫번째구간.validateSectionAndAddSection(15, new Station(1L, "판교역"), new Station(3L, "이매역"), sections)
        );
    }
}
