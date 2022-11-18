package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.exception.ErrorMessage.ONE_SECTION_NOT_DELETE;
import static nextstep.subway.exception.ErrorMessage.STATION_NOT_CONTAINS_NOT_DELETE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간 테스트")
class SectionsTest {

    private Station 신림역;
    private Station 강남역;
    private Station 잠실역;
    private Station 왕십리역;

    @BeforeEach
    void init() {
        신림역 = new Station("신림역");
        강남역 = new Station("강남역");
        잠실역 = new Station("잠실역");
        왕십리역 = new Station("왕십리역");
    }

    @Test
    @DisplayName("지하철 구간이 하나인 경우 삭제 불가")
    void delete_one_section() {
        // given
        Sections sections = new Sections();
        sections.addSection(new Section(신림역, 강남역, 10));

        // when && then
        assertThatThrownBy(() -> sections.deleteSection(신림역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ONE_SECTION_NOT_DELETE.getMessage());
    }

    @Test
    @DisplayName("지하철에 없는 구간은 삭제 불가")
    void delete_not_exists_section() {
        // given
        Sections sections = new Sections();
        sections.addSection(new Section(신림역, 강남역, 10));
        sections.addSection(new Section(강남역, 잠실역, 10));

        // given && then
        assertThatThrownBy(() -> sections.deleteSection(왕십리역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(STATION_NOT_CONTAINS_NOT_DELETE.getMessage());
    }

}
