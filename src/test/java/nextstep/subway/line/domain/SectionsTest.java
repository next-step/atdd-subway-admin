package nextstep.subway.line.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 일급컬렉션 관련 기능")
public class SectionsTest {
    @DisplayName("구간을 추가한다.")
    @Test
    void add_normalSuccess() {
        // given
        Sections sections = Sections.valueOf(SectionTest.section_distant100);

        Sections expectSections = Sections.valueOf(SectionTest.section_distant100, SectionTest.section_distant50);

        // when
        sections.add(SectionTest.section_distant50);

        // then
        Assertions.assertThat(sections).isEqualTo(expectSections);
    }

}
