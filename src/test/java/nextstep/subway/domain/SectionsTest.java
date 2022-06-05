package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.dto.SectionResponse;
import org.junit.jupiter.api.Test;

class SectionsTest {
    private final Line line = new Line("신분당선", "bg-red-600");
    private final Station finalUpStation = new Station("강남역");
    private final Station finalDownStation = new Station("정자역");
    private final Station newStation = new Station("양재역");

    private final Long lineDistance = 30L;

    @Test
    void 구간을_추가할_수_있어야_한다() {
        // given
        final Sections sections = new Sections();
        final Section newSection = new Section(line, finalUpStation, finalDownStation, lineDistance);

        // when
        sections.add(newSection);

        // then
        assertThat(sections.sections).contains(newSection);
    }

    @Test
    void 구간_목록을_조회할_수_있어야_한다() {
        // given
        final Sections sections = new Sections();
        final Section section1 = new Section(line, finalUpStation, newStation, 10L);
        final Section section2 = new Section(line, newStation, finalDownStation, lineDistance - 10L);
        sections.add(section1);
        sections.add(section2);

        // when
        final List<SectionResponse> sectionResponses = sections.sections();

        // then
        assertThat(sectionResponses).containsExactly(SectionResponse.of(section1), SectionResponse.of(section2));
    }
}
