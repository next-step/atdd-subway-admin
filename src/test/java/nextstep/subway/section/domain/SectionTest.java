package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

class SectionTest {

    private static final Section section = Section.of(new Station("a"), new Station("b"), 10);

    @Test
    void create() {
        Section newSection = Section.of(new Station("a"), new Station("b"), 10);
        assertThat(newSection).isNotNull();
    }

    @Test
    void modifyForSectionTest() {
        Section newSection = Section.of(new Station("a"), new Station("c"), 5);

        section.modifySectionFor(newSection);

        assertThat(section.getUpStation()).isEqualTo(new Station("c"));
    }
}
