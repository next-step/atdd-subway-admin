package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {
    private Section section;

    @BeforeEach
    void setUp() {
        Station upStation = new Station("양재역");
        Station downStation = new Station("정자역");

        section = new Section(10, upStation, downStation);
    }

    @Test
    void 역_사이에_새로운_구간이_들어오면_새롭게_추가된_역_기준으로_길이가_조정된다() {
        // given
        Section newSection = new Section(7, new Station("양재역"), new Station("판교역"));
        // when
        section.repair(newSection);
        // then
        assertThat(section.getDistance()).isEqualTo(3);
    }

}