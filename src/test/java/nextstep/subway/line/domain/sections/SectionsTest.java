package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    @DisplayName("중복 없이 역 ID 목록을 가져올 수 있다.")
    @Test
    void getStationsTest() {
        Long station1Id = 1L;
        Long station2Id = 2L;
        Long station3Id = 3L;
        int expectedSize = 3;

        Sections sections = new Sections();
        sections.initFirstSection(new Section(1L, station1Id, station2Id, 3L));
        sections.initFirstSection(new Section(2L, station2Id, station3Id, 5L));

        List<Long> stationIds = sections.getStationIdsWithoutDup();

        assertThat(stationIds).hasSize(expectedSize);
    }

    @DisplayName("초기화되지 않은 Sections에 Section 추가 시 예외 발생")
    @Test
    void addFailTest() {
        Sections sections = new Sections();

        assertThatThrownBy(() -> sections.addSection(new Section(1L, 2L, 3L)))
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
}