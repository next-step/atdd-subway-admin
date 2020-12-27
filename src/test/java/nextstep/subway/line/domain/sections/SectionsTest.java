package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
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

    @DisplayName("제시된 Section과 연결할 구간을 찾을 수 있다. (단, 종점 추가는 고려하지 않는다.")
    @ParameterizedTest
    @MethodSource("findTargetSectionTestResource")
    void findTargetSectionTest(Section newSection, Section foundSection) {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L)
        )));

        assertThat(sections.findTargetSection(newSection)).isEqualTo(foundSection);
    }
    public static Stream<Arguments> findTargetSectionTestResource() {
        return Stream.of(
                Arguments.of(
                        new Section(4L, 2L, 5L),
                        new Section(1L, 2L, 10L)
                ),
                Arguments.of(
                        new Section(1L, 4L, 5L),
                        new Section(1L, 2L, 10L)
                ),
                Arguments.of(
                        new Section(2L, 4L, 5L),
                        new Section(2L, 3L, 10L)
                ),
                Arguments.of(
                        new Section(4L, 3L, 5L),
                        new Section(2L, 3L, 10L)
                )
        );
    }

    @DisplayName("기존 구간과 연결할 구간을 찾지 못한 경우 예외 발생")
    @ParameterizedTest
    @MethodSource("findTargetSectionFailTestResource")
    void findTargetSectionFailTest(Section newSection) {
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L)
        )));

        assertThatThrownBy(() -> sections.findTargetSection(newSection))
                .isInstanceOf(TargetSectionNotFoundException.class);
    }
    public static Stream<Arguments> findTargetSectionFailTestResource() {
        return Stream.of(
                // 상행종점역 추가
                Arguments.of(new Section(4L, 1L, 10L)),
                // 하행종점역 추가
                Arguments.of(new Section(3L, 4L, 10L)),
                // 아예 연관이 없는 역
                Arguments.of(new Section(4L, 5L, 10L)),
                // 둘 다 일치하는 역
                Arguments.of(new Section(2L, 3L, 10L))
        );
    }

    @DisplayName("상행 종점역 구간을 찾아낼 수 있다.")
    @Test
    void findEndUpSectionTest() {
        Section endUpStation = new Section(1L, 2L, 10L);

        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                endUpStation,
                new Section(2L, 3L, 10L),
                new Section (3L, 4L, 10L)
        )));

        assertThat(sections.findEndUpSection()).isEqualTo(endUpStation);
    }

    @DisplayName("한 구간을 기준으로 다음 구간을 찾아낼 수 있다.")
    @Test
    void findNextSectionTest() {
        Section section1 = new Section(1L, 2L, 10L);
        Section section2 = new Section(2L, 3L, 10L);
        Section section3 = new Section (3L, 4L, 10L);

        Sections sections = new Sections(new ArrayList<>(Arrays.asList(section1, section2, section3)));
        Section endUpSection = sections.findEndUpSection();

        assertThat(sections.findNextSection(endUpSection)).isEqualTo(section2);
        assertThat(sections.findNextSection(section2)).isEqualTo(section3);
        assertThat(sections.findNextSection(section3)).isNull();
    }

    @DisplayName("하행 종점역 구간을 찾아낼 수 있다.")
    @Test
    void findEndDownSectionTest() {
        Section endDownStation = new Section(3L, 4L, 10L);

        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, 2L, 10L),
                new Section(2L, 3L, 10L),
                endDownStation
        )));

        assertThat(sections.findEndDownSection()).isEqualTo(endDownStation);
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