package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeOriginalAndAddSectionPolicyTest {
    @DisplayName("기존 구간과 상행역이 겹치는 경우 기존 구간을 변경하고 새로운 구간을 추가할 수 있다.")
    @Test
    void addSectionTestWhenSameUpStationTest() {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L)
        )));
        Section newSection = new Section (2L, 4L, 5L);

        ChangeOriginalAndAddSectionPolicy policy = new ChangeOriginalAndAddSectionPolicy(sections);
        boolean result = policy.addSection(newSection);

        assertThat(result).isTrue();
        assertThat(sections.contains(new Section(1L, 2L, 10L))).isTrue();
        assertThat(sections.contains(new Section(2L, 4L, 5L))).isTrue();
        assertThat(sections.contains(new Section(4L, 3L, 5L))).isTrue();
    }
}