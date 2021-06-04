package nextstep.subway.line.domain;

import static nextstep.subway.section.domain.SectionTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.section.domain.Sections;

public class LineTest {
    public static final Line 수도권_신분당선 = new Line(1L, "수도권_신분당선", "bg-red-600");
    public static final Line 수도권_2호선 = new Line(2L, "수도권_2호선", "bg-green-600");

    @Test
    @DisplayName("생성 테스트")
    void create() {
        // given & when & then
        assertAll(
            () -> assertThat(수도권_신분당선.getColor()).isEqualTo("bg-red-600"),
            () -> assertThat(수도권_신분당선.getName()).isEqualTo("수도권_신분당선")
        );
    }

    @Test
    @DisplayName("구간 추가 테스트")
    void addSection() {
        // given & when
        수도권_신분당선.addSection(강남역_판교역_구간);
        Sections sections = 수도권_신분당선.getSections();
        // then
        assertThat(sections.getSections().size()).isEqualTo(1);
        assertThat(sections.getSections().get(0)).isEqualTo(강남역_판교역_구간);
    }
}
