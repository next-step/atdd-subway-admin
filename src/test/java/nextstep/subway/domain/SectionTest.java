package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SectionTest {
    @Test
    void 노선과_상행역과_하행역과_거리로_구간이_생성되어야_한다() {
        // when
        final Section section = givenSection();

        // then
        assertThat(section).isNotNull();
        assertThat(section).isInstanceOf(Section.class);
    }

    private Section givenSection() {
        final Line line = new Line("신분당선", "bg-red-600");
        final Station upStation = new Station("강남역");
        final Station downStation = new Station("정자역");
        final Long distance = 30L;
        return new Section(line, upStation, downStation, distance);
    }
}
