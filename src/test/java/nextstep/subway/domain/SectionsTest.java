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
    private final Section givenSection1 = new Section(line, finalUpStation, newStation, 10L);
    private final Section givenSection2 = new Section(line, newStation, finalDownStation, lineDistance - 10L);

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
        sections.add(givenSection1);
        sections.add(givenSection2);

        // when
        final List<SectionResponse> sectionResponses = sections.sections();

        // then
        assertThat(sectionResponses.size()).isEqualTo(2);
        assertThat(sectionResponses)
                .containsExactly(
                        new SectionResponse(givenSection1.getLine().getName(), givenSection1.getUpStation().getName(),
                                givenSection1.getDownStation().getName(), givenSection1.getDistance()),
                        new SectionResponse(givenSection2.getLine().getName(), givenSection2.getUpStation().getName(),
                                givenSection2.getDownStation().getName(), givenSection2.getDistance()));
    }

    @Test
    void 상행선으로_구간을_조회할_수_있어야_한다() {
        // given
        final Sections sections = new Sections();
        sections.add(givenSection1);
        sections.add(givenSection2);

        // when
        Section section = sections.getByUpStation(finalUpStation);

        // then
        assertThat(section).isEqualTo(givenSection1);
    }

    @Test
    void 하행선으로_구간을_조회할_수_있어야_한다() {
        // given
        final Sections sections = new Sections();
        sections.add(givenSection1);
        sections.add(givenSection2);

        // when
        Section section = sections.getByDownStation(newStation);

        // then
        assertThat(section).isEqualTo(givenSection1);
    }
}
