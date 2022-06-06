package nextstep.subway.section;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    Station 왕십리역;
    Station 서울숲역;
    Station 선릉역;
    Station 도곡역;
    Sections sections = new Sections();

    @BeforeEach
    void setup() {
        왕십리역 = new Station("왕십리역");
        서울숲역 = new Station("서울숲역");
        선릉역 = new Station("선릉역");
        도곡역 = new Station("도곡역");

        sections.add(new Section(null, 왕십리역, 0));
    }

    @Test
    void 구간_추가_하행_상행_일치() {
        sections.add(new Section(왕십리역, 선릉역, 7));

        assertThat(sections.getList()).contains(
                new Section(null, 왕십리역, 0),
                new Section(왕십리역, 선릉역, 7));
    }

    @Test
    void 구간_추가_상행끼리_일치() {
        sections.add(new Section(왕십리역, 선릉역, 7));

        sections.add(new Section(왕십리역, 서울숲역, 4));

        assertThat(sections.getList()).contains(
                new Section(null, 왕십리역, 0),
                new Section(왕십리역, 서울숲역, 4),
                new Section(서울숲역, 선릉역, 3));
    }

    @Test
    void 구간_추가_하행끼리_일치() {
        sections.add(new Section(왕십리역, 선릉역, 7));

        sections.add(new Section(서울숲역, 선릉역, 3));

        assertThat(sections.getList()).contains(
                new Section(null, 왕십리역, 0),
                new Section(왕십리역, 서울숲역, 4),
                new Section(서울숲역, 선릉역, 3));
    }

    @Test
    void 구간_추가_길이_예외() {
        sections.add(new Section(왕십리역, 선릉역, 7));

        assertThatThrownBy(() -> sections.add(new Section(왕십리역, 서울숲역, 7)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 구간_추가_중복_예외() {
        sections.add(new Section(왕십리역, 선릉역, 7));

        assertThatThrownBy(() -> sections.add(new Section(왕십리역, 선릉역, 3)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 구간_추가_역_미일치_예외() {
        sections.add(new Section(왕십리역, 선릉역, 7));

        assertThatThrownBy(() -> sections.add(new Section(서울숲역, 도곡역, 3)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 역_목록_순서대로_1() {
        sections.add(new Section(왕십리역, 선릉역, 7));

        assertThat(sections.getStationsInOrder())
                .containsExactly(왕십리역, 선릉역);
    }

    @Test
    void 역_목록_순서대로_2() {
        sections.add(new Section(왕십리역, 도곡역, 7));
        sections.add(new Section(왕십리역, 서울숲역, 3));
        sections.add(new Section(선릉역, 도곡역, 2));

        assertThat(sections.getStationsInOrder())
                .containsExactly(왕십리역, 서울숲역, 선릉역, 도곡역);
    }
}
