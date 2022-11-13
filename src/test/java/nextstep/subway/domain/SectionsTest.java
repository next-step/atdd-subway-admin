package nextstep.subway.domain;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SectionsTest {

    Station 하행종점역;
    Station 상행종점역;
    Line 노선;

    @BeforeEach
    void setUp() {
        하행종점역 = new Station("신촌");
        상행종점역 = new Station("합정역");
        노선 = new Line("2호선", "blue", 상행종점역, 하행종점역, 10);
    }

    @Test
    void 상행역_기준_새로운_구간을_역_사이에_추가할_수_있다() {
        Station 기준_상행역 = new Station("홍대입구");
        Station 하행역 = new Station("당산");
        Section 구간 = new Section(노선, 기준_상행역, 하행역, 10);

        Station 새로운_하행역 = new Station("신촌");
        List<Section> 새로운_구간 = 구간.addSection(기준_상행역, 새로운_하행역, 4);

        assertThat(새로운_구간)
                .isEqualTo(Lists.newArrayList(
                        new Section(노선, 기준_상행역, 새로운_하행역, 4),
                        new Section(노선, 새로운_하행역, 하행역, 6)
                        ));

    }

    @Test
    void 하행역_기준_새로운_구간을_역_사이에_추가할_수_있다() {
        Station 상행역 = new Station("홍대입구");
        Station 기준_하행역 = new Station("당산");
        Section 구간 = new Section(노선, 상행역, 기준_하행역, 10);

        Station 새로운_하행역 = new Station("신촌");
        List<Section> 새로운_구간 = 구간.addSection(새로운_하행역, 기준_하행역, 4);

        assertThat(새로운_구간)
                .isEqualTo(Lists.newArrayList(
                        new Section(노선, 상행역, 새로운_하행역, 6),
                        new Section(노선, 새로운_하행역, 기준_하행역, 4)
                        ));

    }

}