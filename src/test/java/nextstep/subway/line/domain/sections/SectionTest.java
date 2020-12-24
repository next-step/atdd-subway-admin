package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.MergeSectionFailException;
import nextstep.subway.line.domain.sections.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    @DisplayName("Section끼리 상행역이 일치하는지 확인할 수 있다.")
    @Test
    void isUpStationSameTest() {
        Section section1 = new Section(1L, 2L, 10L);
        Section section2 = new Section(1L, 3L, 10L);

        assertThat(section1.isSameUpStation(section2)).isTrue();
    }

    @DisplayName("Section끼리 하행역이 일치하는지 확인할 수 있다.")
    @Test
    void isDownStationSameTest() {
        Section section1 = new Section(1L, 2L, 10L);
        Section section2 = new Section(3L, 2L, 10L);

        assertThat(section1.isSameDownStation(section2)).isTrue();
    }

    @DisplayName("접점이 한 개 있는 Section끼리 병합 할 수 있다.")
    @ParameterizedTest
    @MethodSource("mergeTestResource")
    void mergeTest(Section thatSection, Section mergedSection) {
        Section thisSection = new Section(1L, 2L, 5L);

        Section merged = thisSection.merge(thatSection);

        assertThat(merged).isEqualTo(mergedSection);
    }
    public static Stream<Arguments> mergeTestResource() {
        return Stream.of(
                Arguments.of(
                        new Section(2L, 3L, 5L),
                        new Section(1L, 3L, 10L)
                ),
                Arguments.of(
                        new Section(3L, 1L, 5L),
                        new Section(3L, 2L, 10L)
                )
        );
    }

    @DisplayName("접점이 두개거나 없는 경우 Section을 병합할 수 없다.")
    @ParameterizedTest
    @MethodSource("mergeFailTestResource")
    void mergeFailTest(Section thatSection) {
        Section thisSection = new Section(1L, 2L, 5L);

        assertThatThrownBy(() -> thisSection.merge(thatSection))
                .isInstanceOf(MergeSectionFailException.class);
    }
    public static Stream<Arguments> mergeFailTestResource() {
        return Stream.of(
                Arguments.of(new Section(1L, 2L, 10L)),
                Arguments.of(new Section(2L, 1L, 10L)),
                Arguments.of(new Section(3L, 4L, 10L))
        );
    }
}