package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
import nextstep.subway.line.domain.exceptions.InvalidStationDeleteTryException;
import nextstep.subway.line.domain.exceptions.MergeSectionFailException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    @DisplayName("초기화되지 않은 Sections에 Section 추가 시 예외 발생")
    @Test
    void addFailTest() {
        Sections sections = new Sections();

        assertThatThrownBy(() -> sections.addEndSection(new Section(1L, 2L, 3L)))
                .isInstanceOf(InvalidSectionsActionException.class);
    }

    @DisplayName("기존 구간과 상행역이나 하행역이 겹치는 경우 기존 구간을 변경하고 새로운 구간을 추가할 수 있다.")
    @ParameterizedTest
    @MethodSource("addSectionTestWhenSameUpStationTestResource")
    void addSectionTestWhenSameUpStationTest(Sections sections, Section newSection, List<Section> changedSections) {
        boolean result = sections.addNotEndSection(newSection);

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

    @DisplayName("접점이 있는 구간끼리 병합할 수 있다.")
    @ParameterizedTest
    @MethodSource("mergeTestResource")
    void mergeTest(Long targetStation, List<Section> sectionsValue, List<Section> expectedSections) {
        Sections sections = new Sections(new ArrayList<>(sectionsValue));

        boolean result = sections.mergeSectionsByStation(targetStation);

        assertThat(result).isTrue();
        assertThat(sections.containsAll(expectedSections)).isTrue();
    }
    public static Stream<Arguments> mergeTestResource() {
        return Stream.of(
                Arguments.of(
                        4L,
                        Arrays.asList(
                                new Section(1L, 4L, 10L),
                                new Section(4L, 3L, 10L),
                                new Section (3L, 2L, 10L)
                        ),
                        Arrays.asList(
                                new Section(1L, 3L, 20L),
                                new Section (3L, 2L, 10L)
                        )
                ),
                Arguments.of(
                        3L,
                        Arrays.asList(
                                new Section(1L, 4L, 10L),
                                new Section(4L, 3L, 10L),
                                new Section (3L, 2L, 10L)
                        ),
                        Arrays.asList(
                                new Section(1L, 4L, 10L),
                                new Section (4L, 2L, 20L)
                        )
                )
        );
    }

    @DisplayName("목표 역과 연관된 구간이 2개가 아닐 경우 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("mergeFailTestResource")
    void mergeFailTest(Long targetStation, List<Section> sectionsValue) {
        Sections sections = new Sections(new ArrayList<>(sectionsValue));

        assertThatThrownBy(() -> sections.mergeSectionsByStation(targetStation))
                .isInstanceOf(MergeSectionFailException.class);
    }
    public static Stream<Arguments> mergeFailTestResource() {
        return Stream.of(
                Arguments.of(
                        1L,
                        Arrays.asList(
                                new Section(1L, 4L, 10L),
                                new Section(4L, 3L, 10L),
                                new Section (3L, 2L, 10L)
                        )
                ),
                Arguments.of(
                        2L,
                        Arrays.asList(
                                new Section(1L, 4L, 10L),
                                new Section(4L, 3L, 10L),
                                new Section (3L, 2L, 10L)
                        )
                ),
                Arguments.of(
                        5L,
                        Arrays.asList(
                                new Section(1L, 4L, 10L),
                                new Section(4L, 3L, 10L),
                                new Section (3L, 2L, 10L)
                        )
                )
        );
    }

    @DisplayName("종점역을 제거할 수 있다.")
    @ParameterizedTest
    @MethodSource("deleteEndStationTestResource")
    void deleteEndStationTest(Long deleteTargetId, List<Section> expectedRemainedSections) {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L),
                new Section (3L, 4L, 10L)
        )));

        boolean result = sections.deleteEndStation(deleteTargetId);

        assertThat(result).isTrue();
        assertThat(sections.containsAll(expectedRemainedSections)).isTrue();
    }
    public static Stream<Arguments> deleteEndStationTestResource() {
        return Stream.of(
                Arguments.of(
                        1L,
                        Arrays.asList(
                                new Section(2L, 3L, 10L),
                                new Section (3L, 4L, 10L)
                        )
                ),
                Arguments.of(
                        4L,
                        Arrays.asList(
                                new Section(1L, 2L, 10L),
                                new Section(2L, 3L, 10L)
                        )
                )
        );
    }

    @DisplayName("종점이 아닌 역을 종점 제거로 삭제 시도 시 예외 발생")
    @ParameterizedTest
    @ValueSource(longs = { 2L, 3L })
    void deleteEndStationFailTest(Long notEndStationId) {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L),
                new Section(3L, 4L, 10L)
        )));

        assertThatThrownBy(() -> sections.deleteEndStation(notEndStationId))
                .isInstanceOf(InvalidStationDeleteTryException.class);
    }

    @DisplayName("기존역과 상행역, 하행역 모두 겹치지 않는 경우 예외 발생")
    @Test
    void addSectionFailTest() {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L)
        )));
        Section notMatchAnySection = new Section(4L, 5L, 100L);

        assertThatThrownBy(() -> sections.addNotEndSection(notMatchAnySection))
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

        assertThatThrownBy(() -> sections.addNotEndSection(tooLongSection))
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

        assertThatThrownBy(() -> sections.addNotEndSection(invalidSection))
                .isInstanceOf(TargetSectionNotFoundException.class);
    }
    public static Stream<Arguments> addSectionFailByAlreadyInLineStationsTestResource() {
        return Stream.of(
                Arguments.of(new Section(1L, 2L, 5L)),
                Arguments.of(new Section(1L, 3L, 5L))
        );
    }

    @DisplayName("새로운 종점 구간을 추가하는 경우 종점 구간을 변경할 수 있다.")
    @ParameterizedTest
    @MethodSource("addEndSectionTestResource")
    void addEndSectionTest(Section newEndSection) {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L)
        )));

        boolean result = sections.addEndSection(newEndSection);

        assertThat(result).isTrue();
    }
    public static Stream<Arguments> addEndSectionTestResource() {
        return Stream.of(
                Arguments.of(new Section(4L, 1L, 10L)),
                Arguments.of(new Section(3L, 4L, 10L))
        );
    }

    @DisplayName("종점 구간이 아닌 구간으로 종점 구간 추가를 시도할 수 없다.")
    @Test
    void addEndSectionFailTest() {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L)
        )));

        assertThatThrownBy(() -> sections.addEndSection(new Section(4L, 2L, 5L)))
                .isInstanceOf(InvalidSectionsActionException.class)
                .hasMessage("종점이 아닌 구간으로 종점 구간 추가를 수행할 수 없습니다.");
    }
}