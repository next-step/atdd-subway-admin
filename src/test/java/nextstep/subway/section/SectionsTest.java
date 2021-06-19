package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("지하철 구간 리스트 기능 테스트")
public class SectionsTest {
    private Line 일호선;
    private Station 구일역;
    private Station 구로역;
    private Station 신도림역;
    private Station 영등포역;
    private Station 회기역;

    @BeforeEach
    void setUp() {
        일호선 = new Line("일호선", "blue");
        구일역 = new Station("구일역");
        구로역 = new Station("구로역");
        신도림역 = new Station("신도림역");
        영등포역 = new Station("영등포역");
        회기역 = new Station("회기역");
    }

    @DisplayName("구간내에 역의 존재 여부 확인한다")
    @Test
    void existSectionInStation() {
        Sections sections = 구간이_노선에_추가됨(구일역, 구로역, 100);

        구간내에_역이_존재함(sections);

        구간내에_역이_존재하지_않음(sections);
    }

    @DisplayName("구간을 다양하게 추가하고 역을 순서대로 보여준다")
    @Test
    void addSection() {
        Sections sections = 구간_순서없이_여러개_추가();

        구간내_역이_순서대로_정렬됨(sections);
    }

    @DisplayName("구간을 삭제하고 역을 순서대로 보여준다")
    @Test
    void deleteSection() {
        Sections sections = 구간_순서없이_여러개_추가();

        구간내_역_삭제됨(sections, 신도림역);
        sections = 구간내_역_삭제됨(sections, 영등포역);

        구간내_역_삭제후_순서대로_정렬됨(sections);
    }

    @DisplayName("상/하행선 둘 다 불일치하는 구간 추가시 경우 예외 발생")
    @Test
    void addSectionException1() {
        Sections sections = 구간이_노선에_추가됨(구일역, 구로역, 100);
        
        상하행선_둘다_불일치_예외_발생(sections);
    }

    @DisplayName("동일한 구간 추가 할 경우 예외 발생")
    @Test
    void addSectionException2() {
        Sections sections = 구간이_노선에_추가됨(구일역, 구로역, 100);
        
        동일한_구간_추가_예외_발생(sections);
    }

    @DisplayName("하나 남은 구간 제거 시 예외 발생")
    @Test
    void deleteSectionException() {
        Sections sections = 구간이_노선에_추가됨(구일역, 구로역, 100);

        하나_남은_구간_제거시_예외_발생(sections);
    }

    private Sections 구간_순서없이_여러개_추가() {
        Sections sections = new Sections();
        sections.addSection(SectionTest.구간을_생성함(구일역, 구로역, 100));
        sections.addSection(SectionTest.구간을_생성함(구일역, 영등포역, 30));
        sections.addSection(SectionTest.구간을_생성함(구로역, 신도림역, 350));
        sections.addSection(SectionTest.구간을_생성함(영등포역, 회기역, 50));
        return sections;
    }

    private Sections 구간이_노선에_추가됨(Station upStation, Station downStation, int distance) {
        Sections sections = new Sections();
        sections.addSection(SectionTest.구간을_생성함(upStation, downStation,distance));
        return sections;
    }

    private void 구간내에_역이_존재함(Sections sections) {
        assertThat(sections.isStationExist(구일역)).isTrue();
        assertThat(sections.isStationExist(구로역)).isTrue();
    }

    private void 구간내에_역이_존재하지_않음(Sections sections) {
        assertThat(sections.isStationExist(신도림역)).isFalse();
        assertThat(sections.isStationExist(영등포역)).isFalse();
    }

    private void 구간내_역이_순서대로_정렬됨(Sections sections) {
        List<Station> stations = sections.assembleStations();
        assertThat(stations).containsExactly(구일역, 영등포역, 회기역, 구로역, 신도림역);
    }

    private Sections 구간내_역_삭제됨(Sections sections, Station deleteStation) {
        sections.deleteSection(일호선, deleteStation);
        return sections;
    }

    private void 구간내_역_삭제후_순서대로_정렬됨(Sections sections) {
        List<Station> deletedSections = sections.assembleStations();
        assertThat(deletedSections).containsExactly(구일역, 회기역, 구로역);
    }

    private void 하나_남은_구간_제거시_예외_발생(Sections sections) {
        assertThatThrownBy(() -> {
            sections.deleteSection(일호선, 구일역);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("역을 제거할 수 없습니다.");
    }

    private void 동일한_구간_추가_예외_발생(Sections sections) {
        assertThatThrownBy(() -> {
            sections.addSection(SectionTest.구간을_생성함(구일역, 구로역, 100));
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("동일한 구간은 추가할 수 없습니다.");
    }

    private void 상하행선_둘다_불일치_예외_발생(Sections sections) {
        assertThatThrownBy(() -> {
            sections.addSection(SectionTest.구간을_생성함(신도림역, 영등포역, 100));
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("상/하행선 둘 중 하나는 일치해야 합니다.");
    }
}
