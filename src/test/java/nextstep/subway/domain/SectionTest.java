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

    @Test
    void 상행역을_변경할_수_있어야_한다() {
        // given
        final Section section = givenSection();
        final Station newStation = new Station("신논현역");
        final Long distance = 10L;

        // when
        section.updateUpStation(newStation, distance);

        // then
        assertThat(section.getUpStation()).isEqualTo(newStation);
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    @Test
    void 하행역을_변경할_수_있어야_한다() {
        // given
        final Section section = givenSection();
        final Station newStation = new Station("신논현역");
        final Long distance = 10L;

        // when
        section.updateDownStation(newStation, distance);

        // then
        assertThat(section.getDownStation()).isEqualTo(newStation);
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    private Section givenSection() {
        final Line line = new Line("신분당선", "bg-red-600");
        final Station upStation = new Station("강남역");
        final Station downStation = new Station("정자역");
        final Long distance = 30L;
        return new Section(line, upStation, downStation, distance);
    }
}
