package nextstep.subway.section.domain;

import nextstep.subway.exception.IncorrectSectionException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 관련")
class SectionsTest {

    @DisplayName("역 사이 구간 추가")
    @Test
    public void addSectioInMiddle() {
        //given
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Station 교대역 = new Station("교대역");
        Section 종점사이 = Section.of(강남역, 사당역, 4000);
        Section 추가구간 = Section.of(강남역, 교대역, 1000);
        sections.add(종점사이);

        //when
        sections.addInMiddle(추가구간);

        //then
        assertThat(sections.getStations().values()).containsExactly(강남역, 교대역, 사당역);
    }

    @DisplayName("역 사이 구간 추가시 잘못된 길이로 생성 실패")
    @Test
    public void addSectioInMiddleErrorBecauseofDistance() {
        //given
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Station 교대역 = new Station("교대역");
        Section 종점사이 = Section.of(강남역, 사당역, 500);
        Section 추가구간 = Section.of(강남역, 교대역, 1000);
        sections.add(종점사이);

        //when&&then
        assertThatThrownBy(() -> sections.addInMiddle(추가구간)).isInstanceOf(IncorrectSectionException.class);
    }

    @DisplayName("상행 확장")
    @Test
    public void addSectioOnTop() {
        //given
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Station 교대역 = new Station("교대역");
        Section 종점사이 = Section.of(교대역, 사당역, 4000);
        Section 추가구간 = Section.of(강남역, 교대역, 1000);
        sections.add(종점사이);

        //when
        sections.addOnTop(추가구간);

        //then
        assertThat(sections.getStations().values()).containsExactly(강남역, 교대역, 사당역);
    }

    @DisplayName("하행 확장")
    @Test
    public void addSectioUnderBottom() {
        //given
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Station 교대역 = new Station("교대역");
        Section 종점사이 = Section.of(강남역, 교대역, 4000);
        Section 추가구간 = Section.of(교대역, 사당역, 1000);
        sections.add(종점사이);

        //when
        sections.addBelow(추가구간);

        //then
        assertThat(sections.getStations().values()).containsExactly(강남역, 교대역, 사당역);
    }

    @DisplayName("중간역 삭제")
    @Test
    void deleteBothSectionsByStation() {
        //given
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Station 교대역 = new Station("교대역");
        Section 종점사이 = Section.of(강남역, 사당역, 4000);
        Section 추가구간 = Section.of(강남역, 교대역, 1000);
        sections.add(종점사이);
        sections.addInMiddle(추가구간);

        //when
        sections.deleteMiddleSectionBy(교대역);

        //then
        assertThat(sections.getStations().values()).containsExactly(강남역,사당역);
    }

    @DisplayName("중간역 삭제")
    @Test
    void deleteAndSortingTest() {
        //given
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Station 교대역 = new Station("교대역");
        Section 종점사이 = Section.of(강남역, 사당역, 4000);
        Section 추가구간 = Section.of(강남역, 교대역, 1000);
        sections.add(종점사이);
        sections.addInMiddle(추가구간);

        //when
        sections.deleteMiddleSectionBy(교대역);

        //then
        assertThat(sections.getStations().values()).containsExactly(강남역,사당역);
    }

    @DisplayName("상행종점 삭제")
    @Test
    void deleteFirstSectionsByStation() {
        //given
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Station 교대역 = new Station("교대역");
        Section 종점사이 = Section.of(강남역, 사당역, 4000);
        Section 추가구간 = Section.of(강남역, 교대역, 1000);
        sections.add(종점사이);
        sections.addInMiddle(추가구간);

        //when
        sections.deleteFirstSectionBy(강남역);

        //then
        assertThat(sections.getStations().values()).containsExactly(교대역,사당역);
    }

    @DisplayName("하행종점 삭제")
    @Test
    void deleteLastSectionsByStation() {
        //given
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Station 교대역 = new Station("교대역");
        Section 종점사이 = Section.of(강남역, 사당역, 4000);
        Section 추가구간 = Section.of(강남역, 교대역, 1000);
        sections.add(종점사이);
        sections.addInMiddle(추가구간);

        //when
        sections.deleteLastSectionBy(사당역);

        //then
        assertThat(sections.getStations().values()).containsExactly(강남역,교대역);
    }

    @DisplayName("종점구간만 있을때 삭제 에러")
    @Test
    void deleteOnlyOneSectionsByStationError() {
        //given
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Section 종점사이 = Section.of(강남역, 사당역, 4000);
        sections.add(종점사이);
        Line 이호선 = new Line("2호선","green");
        이호선.addSection(종점사이);

        //when
        assertThatThrownBy( () -> 이호선.deleteSectioByStation(사당역)).isInstanceOf(IncorrectSectionException.class);
    }

    @DisplayName("노선에 없는 역으로 삭제 에러")
    @Test
    void deleteSectionsByNoUseStationError() {
        //given
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Section 종점사이 = Section.of(강남역, 사당역, 4000);
        sections.add(종점사이);
        Line 이호선 = new Line("2호선","green");
        이호선.addSection(종점사이);
        Station 종로역 = new Station("종로역");

        //when
        assertThatThrownBy( () -> 이호선.deleteSectioByStation(종로역)).isInstanceOf(IncorrectSectionException.class);
    }

}