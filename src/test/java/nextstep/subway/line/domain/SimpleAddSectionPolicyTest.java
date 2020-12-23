package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleAddSectionPolicyTest {
    @DisplayName("단순하게 Section을 추가하고 추가한 결과의 성공여부를 알려준다.")
    @Test
    void addTest() {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L)
        )));

        SimpleAddSectionPolicy simpleAddSectionPolicy = new SimpleAddSectionPolicy(sections);
        boolean savedResult = simpleAddSectionPolicy.addSection(
                new Section(4L, 1L, 10L)
        );

        assertThat(savedResult).isTrue();
    }
}