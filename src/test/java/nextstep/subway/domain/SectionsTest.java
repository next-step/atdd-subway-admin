package nextstep.subway.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.common.Messages.DUPLICATE_SECTION_ERROR;
import static nextstep.subway.common.Messages.NOT_MATCH_STATION_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class SectionsTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 교대역;
    private int 거리;
    private Section 강남_역삼_지하철_구간;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        교대역 = new Station("교대역");
        거리 = 10;

        강남_역삼_지하철_구간 = new Section(거리, 강남역, 역삼역);
    }

    @Test
    @DisplayName("지하철 구간 등록 테스트")
    void addSections() {
        Sections sections = new Sections();
        sections.addSections(강남_역삼_지하철_구간);

        Assertions.assertAll(
                () -> assertThat(sections.getSections()).size().isEqualTo(1),
                () -> assertThat(sections.getSections()).contains(강남_역삼_지하철_구간)
        );
    }

    @Test
    @DisplayName("지하철 구간 중복 등록 실패 테스트")
    void addSectionsDuplicateSection() {
        Sections sections = new Sections();
        sections.addSections(강남_역삼_지하철_구간);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> sections.addSections(강남_역삼_지하철_구간))
                .withMessageContaining(DUPLICATE_SECTION_ERROR);
    }

    @Test
    @DisplayName("지하철 구간 등록시 해당되지 않는 지하철 구간 실패 테스트")
    void addSectionsNotMatchStation() {
        Sections sections = new Sections();
        sections.addSections(강남_역삼_지하철_구간);

        Station 신도림역 = new Station("신도림");
        Station 대림역 = new Station("대림");

        Section 신도림_대림_지하철_구간 = new Section(거리, 신도림역, 대림역);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> sections.addSections(신도림_대림_지하철_구간))
                .withMessageContaining(NOT_MATCH_STATION_ERROR);
    }

    @Test
    @DisplayName("등록되어있는 지하철 조회 테스트")
    void getSections() {
        Sections sections = new Sections();
        Section 교대역_강남역_지하철_구간 = new Section(거리, 교대역, 강남역);

        sections.addSections(강남_역삼_지하철_구간);
        sections.addSections(교대역_강남역_지하철_구간);

        List<Station> stations = sections.getStations();

        Assertions.assertAll(
                () -> assertThat(stations).size().isEqualTo(3),
                () -> assertThat(stations).contains(강남역, 교대역, 역삼역)
        );
    }
}
