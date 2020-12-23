package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;
import nextstep.subway.line.domain.exceptions.TooLongSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ChangeOriginalAndAddSectionPolicyTest {
    @DisplayName("기존 구간과 상행역이나 하행역이 겹치는 경우 기존 구간을 변경하고 새로운 구간을 추가할 수 있다.")
    @ParameterizedTest
    @MethodSource("addSectionTestWhenSameUpStationTestResource")
    void addSectionTestWhenSameUpStationTest(Sections sections, Section newSection, List<Section> changedSections) {
        AddSectionPolicy policy = new ChangeOriginalAndAddSectionPolicy(sections);
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

    @DisplayName("기존역과 상행역, 하행역 모두 겹치지 않는 경우 예외 발생")
    @Test
    void addSectionFailTest() {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L)
        )));
        Section notMatchAnySection = new Section(4L, 5L, 100L);

        AddSectionPolicy addSectionPolicy = new ChangeOriginalAndAddSectionPolicy(sections);

        assertThatThrownBy(() -> addSectionPolicy.addSection(notMatchAnySection))
                .isInstanceOf(TargetSectionNotFoundException.class);
    }

    @DisplayName("추가할 구간의 거리보다 더 긴 거리로 새로운 Sectoin 추가 시도 시 예외 발생")
    @ParameterizedTest
    @ValueSource(longs = { 10L, 11L })
    void addSectionFailByDistanceTest(Long invalidDistance) {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L)
        )));
        Section tooLongSection = new Section(4L, 3L, invalidDistance);

        AddSectionPolicy addSectionPolicy = new ChangeOriginalAndAddSectionPolicy(sections);

        assertThatThrownBy(() -> addSectionPolicy.addSection(tooLongSection))
                .isInstanceOf(TooLongSectionException.class);
    }

    @DisplayName("이미 지하철 노선에 존재하는 역들로만 구성된 신규 Section 추가 시도 시 예외 발생")
    @ParameterizedTest
    @MethodSource("addSectionFailByAlreadyInLineStationsTestResource")
    void addSectionFailByAlreadyInLineStationsTest(Section invalidSection) {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L)
        )));

        AddSectionPolicy addSectionPolicy = new ChangeOriginalAndAddSectionPolicy(sections);

        assertThatThrownBy(() -> addSectionPolicy.addSection(invalidSection))
                .isInstanceOf(TargetSectionNotFoundException.class);
    }
    public static Stream<Arguments> addSectionFailByAlreadyInLineStationsTestResource() {
        return Stream.of(
                Arguments.of(new Section(1L, 2L, 5L)),
                Arguments.of(new Section(1L, 3L, 5L))
        );
    }
}