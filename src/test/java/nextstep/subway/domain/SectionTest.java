package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

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
    void 새로운_구간을_추가할_수_있는지_확인() {
        Section 구간 = new Section(노선, 상행종점역, 하행종점역, 10);

        Station 새로운_하행역 = new Station("신촌");

        assertThat(구간.canAddSection(상행종점역, 새로운_하행역)).isTrue();
    }

}