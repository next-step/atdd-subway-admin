package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 리스트")
class SectionsTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;

    private Section 강남_역삼_구간;
    private Section 역삼_선릉_구간;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");

        강남_역삼_구간 = Section.of(강남역, 역삼역, 5);
        역삼_선릉_구간 = Section.of(역삼역, 선릉역, 10);
    }

    @Test
    @DisplayName("구간을 추가하면 구간 리스트에 구간이 추가된다.")
    void 구간_추가() {
        Sections sections = new Sections();
        sections.add(강남_역삼_구간);

        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("상행역이 같은 구간을 추가하면 ")
    void 상행역이_같은_구간_추가() {
        Sections sections = new Sections();

        Section 강남_선릉_구간 = Section.of(강남역, 선릉역, 15);
        sections.add(강남_선릉_구간);
        sections.add(강남_역삼_구간);

        assertThat(sections.get()).containsExactlyInAnyOrder(강남_역삼_구간, 역삼_선릉_구간);
    }

    @Test
    @DisplayName("하행역이 같은 구간을 추가하면 ")
    void 하행역이_같은_구간_추가() {
        Sections sections = new Sections();

        Section 강남_선릉_구간 = Section.of(강남역, 선릉역, 15);
        sections.add(강남_선릉_구간);
        sections.add(역삼_선릉_구간);

        assertThat(sections.get()).containsExactlyInAnyOrder(강남_역삼_구간, 역삼_선릉_구간);
    }
}
