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

    @Test
    void 기존에_구간과_새로운_구간이_같은지_확인한다() {
        // given
        Section newSection = new Section(7, new Station("양재역"), new Station("정자역"));
        // when
        boolean result = section.isSame(newSection);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void 기존_구간의_상행역_하행역과_일치하는_역이_하나라도_있는지_확인한다() {
        // given
        Section newSection = new Section(7, new Station("강남역"), new Station("양재역"));
        // when
        boolean result = section.isAnyMatch(newSection);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void 기존_구간의_상행역_하행역과_일치하는_역이_하나도_없는지_확인한다() {
        // given
        Section newSection = new Section(7, new Station("강남역"), new Station("판교역"));
        // when
        boolean result = section.isAnyMatch(newSection);
        // then
        assertThat(result).isFalse();
    }
}