package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeOriginalAndAddSectionPolicyTest {
    @DisplayName("기존 구간과 상행역이나 하행역이 겹치는 경우 기존 구간을 변경하고 새로운 구간을 추가할 수 있다.")
    @ParameterizedTest
    @MethodSource("addSectionTestWhenSameUpStationTestResource")
    void addSectionTestWhenSameUpStationTest(Sections sections, Section newSection, List<Section> changedSections) {
        ChangeOriginalAndAddSectionPolicy policy = new ChangeOriginalAndAddSectionPolicy(sections);
        boolean result = policy.addSection(newSection);

        assertThat(result).isTrue();
        assertThat(sections.containsAll(changedSections)).isTrue();
    }
    public static Stream<Arguments> addSectionTestWhenSameUpStationTestResource() {
        return Stream.of(
               Arguments.of(
                       new Sections(new ArrayList<>(Arrays.asList(
                               new Section(1L, 2L, 10L),
                               new Section(2L, 3L, 10L)
                       ))),
                       new Section (2L, 4L, 5L),
                       Arrays.asList(
                               new Section(1L, 2L, 10L),
                               new Section(2L, 4L, 5L),
                               new Section(4L, 3L, 5L)
                       )
               ),
                Arguments.of(
                        new Sections(new ArrayList<>(Arrays.asList(
                                new Section(1L, 2L, 10L),
                                new Section(2L, 3L, 10L)
                        ))),
                        new Section (4L, 2L, 5L),
                        Arrays.asList(
                                new Section(1L, 4L, 5L),
                                new Section(4L, 2L, 5L),
                                new Section(2L, 3L, 10L)
                        )
                )
        );
    }
}