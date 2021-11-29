package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.Collections;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    void create() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Section section1 = new Section(1L, station1, station2, 1);
        final Section section2 = new Section(2L, station2, station3, 1);

        // when
        final Sections sections = new Sections(Arrays.asList(section1, section2));

        // then
        assertAll(
            () -> assertThat(sections).isNotNull(),
            () -> assertThat(sections.size()).isEqualTo(2)
        );
    }

    @Test
    void add() {
        // given
        final Sections sections = new Sections(Collections.emptyList());

        // when
        final Section newSection = new Section(
            1L,
            new Station(1L, "강남역"),
            new Station(2L, "역삼역"),
            1
        );
        sections.add(newSection);

        // then
        assertAll(
            () -> assertThat(sections).isNotNull(),
            () -> assertThat(sections.size()).isEqualTo(1)
        );
    }

    @Test
    void add_prepend() {
        // given
        final Section firstSection = new Section(
            1L,
            new Station(1L, "강남역"),
            new Station(2L, "역삼역"),
            1
        );
        final Sections sections = new Sections(Arrays.asList(firstSection));

        // when
        final Section newSection = new Section(
            3L,
            new Station(3L, "교대역"),
            new Station(1L, "강남역"),
            1
        );
        sections.add(newSection);

        // then
        assertAll(
            () -> assertThat(sections).isNotNull(),
            () -> assertThat(sections.size()).isEqualTo(2)
        );
    }

    @Test
    void add_append() {
        // given
        final Section lastSection = new Section(
            1L,
            new Station(1L, "강남역"),
            new Station(2L, "역삼역"),
            1
        );
        final Sections sections = new Sections(Arrays.asList(lastSection));

        // when
        final Section newSection = new Section(
            3L,
            new Station(2L, "역삼역"),
            new Station(3L, "선릉역"),
            1
        );
        sections.add(newSection);

        // then
        assertAll(
            () -> assertThat(sections).isNotNull(),
            () -> assertThat(sections.size()).isEqualTo(2)
        );
    }

    @Test
    void add_inBetween_matchingUpStation() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 2);
        final Sections sections = new Sections(Arrays.asList(section));

        // when
        final Station station3 = new Station(3L, "테헤란역");
        final Section newSection = new Section(2L, station1, station3, 1);
        sections.add(newSection);

        // then
        assertAll(
            () -> assertThat(sections).isNotNull(),
            () -> assertThat(sections.size()).isEqualTo(2)
        );
    }

    @Test
    void add_inBetween_matchingUpStation_invalidDistance() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 1);
        final Sections sections = new Sections(Arrays.asList(section));

        // when
        final Station station3 = new Station(3L, "테헤란역");
        final Section newSection = new Section(2L, station1, station3, 1);

        // then
        assertThatThrownBy(
            () -> sections.add(newSection)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_inBetween_matchingDownStation() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 2);
        final Sections sections = new Sections(Arrays.asList(section));

        // when
        final Station station3 = new Station(3L, "테헤란역");
        final Section newSection = new Section(2L, station3, station2, 1);
        sections.add(newSection);

        // then
        assertAll(
            () -> assertThat(sections).isNotNull(),
            () -> assertThat(sections.size()).isEqualTo(2)
        );
    }

    @Test
    void add_inBetween_matchingDownStation_invalidDistance() {
        // given
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section = new Section(1L, station1, station2, 1);
        final Sections sections = new Sections(Arrays.asList(section));

        // when
        final Station station3 = new Station(3L, "테헤란역");
        final Section newSection = new Section(2L, station3, station2, 1);

        // then
        assertThatThrownBy(
            () -> sections.add(newSection)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_existingSection() {
        // given
        final Section section = new Section(
            1L,
            new Station(1L, "강남역"),
            new Station(2L, "역삼역"),
            2
        );
        final Sections sections = new Sections(Arrays.asList(section));

        // when, then
        assertThatThrownBy(
            () -> sections.add(section)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_noMatchingStations() {
        // given
        final Section section = new Section(
            1L,
            new Station(1L, "강남역"),
            new Station(2L, "역삼역"),
            2
        );
        final Sections sections = new Sections(Arrays.asList(section));

        // when
        final Section newSection = new Section(
            2L,
            new Station(3L, "판교역"),
            new Station(4L, "광교역"),
            2
        );

        // then
        assertThatThrownBy(
            () -> sections.add(newSection)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
