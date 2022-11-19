package nextstep.subway.domain;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

    Station 하행종점역;
    Station 상행종점역;
    Line 노선;

    @BeforeEach
    void setUp() {
        하행종점역 = new Station("신촌");
        상행종점역 = new Station("합정");
        노선 = new Line("2호선", "blue", 상행종점역, 하행종점역, 10);
    }

    @Test
    void 구간을_추가하기_위한_구간_분할() {
        Section 구간 = new Section(노선, 상행종점역, 하행종점역, 10);
        Station 새로운_하행역 = new Station("홍대입구");

        List<Section> 분할된_구간 = 구간.splitSection(상행종점역, 새로운_하행역, Distance.valueOf(4));

        assertThat(분할된_구간).isEqualTo(Lists.newArrayList(
                new Section(노선, 상행종점역, 새로운_하행역, 4),
                new Section(노선, 새로운_하행역, 하행종점역, 6)
        ));
    }

}