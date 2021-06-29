package nextstep.subway.section.domain;

import nextstep.subway.exception.CannotRemoveSingleSectionException;
import nextstep.subway.station.domain.Station;
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
        Section 첫번째구간 = new Section(판교역, 이매역, 10);

        Station 모란역 = new Station("모란역");
        Station 야탑역 = new Station("야탑역");
        Section 두번째구간 = new Section(모란역, 야탑역, 20);

        //when
        Sections sections = new Sections(List.of(첫번째구간, 두번째구간));

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
        Section 첫번째구간 = new Section(판교역, 야탑역, 10);

        Station 모란역 = new Station("모란역");
        Section 두번째구간 = new Section(모란역, 야탑역, 20);

        //when
        Sections sections = new Sections(List.of(첫번째구간, 두번째구간));

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
        Section 첫번째구간 = new Section(판교역, 야탑역, 10);

        Sections sections = new Sections(List.of(첫번째구간));

        //when
        assertThrows(CannotRemoveSingleSectionException.class,
                () -> sections.validateAndRemoveSectionByStation(판교역)
        );
    }
}
