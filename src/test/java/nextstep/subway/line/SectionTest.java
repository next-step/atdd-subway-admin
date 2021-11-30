package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    void adjustUpStation() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 2);

        // when
        final Station station3 = new Station(3L, "테헤란역");
        final Section newSection = new Section(2L, station1, station3, 1);
        section.adjustUpStation(newSection);

        // then
        assertAll(
            () -> assertThat(section.getUpStation()).isEqualTo(station3),
            () -> assertThat(section.getDownStation()).isEqualTo(station2),
            () -> assertThat(section.getDistance()).isEqualTo(1)
        );
    }

    @Test
    void adjustUpStation_invalidSection() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 2);

        // when, then
        assertThatThrownBy(
            () -> section.adjustUpStation(section)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void adjustUpStation_invalidDistance() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 2);

        // when
        final Station station3 = new Station(3L, "테헤란역");
        final Section newSection = new Section(2L, station1, station3, 3);

        // then
        assertThatThrownBy(
            () -> section.adjustUpStation(newSection)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void adjustDownStation() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 2);

        // when
        final Station station3 = new Station(3L, "테헤란역");
        final Section newSection = new Section(2L, station3, station2, 1);
        section.adjustDownStation(newSection);

        // then
        assertAll(
            () -> assertThat(section.getUpStation()).isEqualTo(station1),
            () -> assertThat(section.getDownStation()).isEqualTo(station3),
            () -> assertThat(section.getDistance()).isEqualTo(1)
        );
    }

    @Test
    void adjustDownStation_invalidSection() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 2);

        // when, then
        assertThatThrownBy(
            () -> section.adjustDownStation(section)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void adjustDownStation_invalidDistance() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 2);

        // when
        final Station station3 = new Station(3L, "테헤란역");
        final Section newSection = new Section(2L, station3, station2, 3);

        // then
        assertThatThrownBy(
            () -> section.adjustDownStation(newSection)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void hasStation_true() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 1);

        // when, then
        assertAll(
            () -> assertThat(section.hasStation(station1)).isTrue(),
            () -> assertThat(section.hasStation(station2)).isTrue()
        );

    }

    @Test
    void hasStation_false() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 1);

        // when, then
        final Station station3 = new Station(3L, "선릉역");
        assertThat(section.hasStation(station3)).isFalse();
    }
}
