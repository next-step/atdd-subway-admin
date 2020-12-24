package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.InvalidStationDeleteTryException;
import nextstep.subway.line.domain.sections.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    private static final String LINE_NAME = "2호선";
    private static final String LINE_COLOR = "녹색";

    @DisplayName("정책에 따라 알맞는 Section 추가 로직을 수행할 수 있다.")
    @ParameterizedTest
    @MethodSource("addSectionTestResource")
    void addSectionTest() {
        Long initUpStationId = 1L;
        Long initDownStationId = 2L;
        Long initDistance = 10L;
        Line line = new Line(LINE_NAME, LINE_COLOR);
        Section newSection = new Section(3L, 1L,10L);
        line.initFirstSection(initUpStationId, initDownStationId, initDistance);

        boolean result = line.addSection(newSection);

        assertThat(result).isTrue();
    }
    public static Stream<Arguments> addSectionTestResource() {
        return Stream.of(
                // 상행종점역 추가
                Arguments.of(new Section(3L, 1L,10L)),
                // 하행종점역 추가
                Arguments.of(new Section(2L, 3L,10L)),
                // 기존 구간 중 상행역이 일치하는 경우 추가
                Arguments.of(new Section(1L, 3L,5L)),
                // 기존 구간 중 하행역이 일치하는 경우 추가
                Arguments.of(new Section(3L, 2L,5L))
        );
    }

    @DisplayName("남은 구간의 길이가 2 미만일 경우 구간의 역 삭제를 진행할 수 없다.")
    @Test
    void deleteStationOfSectionFailByTooShortSectionTest() {
        Long initUpStationId = 1L;
        Long initDownStationId = 2L;
        Long initDistance = 10L;
        Long deleteTarget = 2L;
        Line line = new Line(LINE_NAME, LINE_COLOR);
        line.initFirstSection(initUpStationId, initDownStationId, initDistance);

        assertThatThrownBy(() -> line.deleteStationOfSection(deleteTarget))
                .isInstanceOf(InvalidStationDeleteTryException.class);
    }

    // TODO: 이후 기능에서 종점도 삭제 가능해질 예정
    @DisplayName("삭제 대상이 종점인 경우 삭제 할 수 없다.")
    @ParameterizedTest
    @ValueSource(longs = { 1L, 3L })
    void deleteStationOfSectionFailWhenToEndStationTest(Long deleteTarget) {
        Long initUpStationId = 1L;
        Long initDownStationId = 2L;
        Long initDistance = 10L;
        Long secondSectionUp = 2L;
        Long secondSectionDown = 3L;
        Line line = new Line(LINE_NAME, LINE_COLOR);
        line.initFirstSection(initUpStationId, initDownStationId, initDistance);
        line.addSection(new Section(secondSectionUp, secondSectionDown, 10L));

        assertThatThrownBy(() -> line.deleteStationOfSection(deleteTarget))
                .isInstanceOf(InvalidStationDeleteTryException.class);
    }

    @DisplayName("구간의 역을 삭제할 수 있다.")
    @Test
    void deleteStationOfSectionTest() {
        Long initUpStationId = 1L;
        Long initDownStationId = 2L;
        Long initDistance = 10L;
        Long secondSectionUp = 2L;
        Long secondSectionDown = 3L;
        Long deleteTarget = 2L;
        Line line = new Line(LINE_NAME, LINE_COLOR);
        line.initFirstSection(initUpStationId, initDownStationId, initDistance);
        line.addSection(new Section(secondSectionUp, secondSectionDown, 10L));

        boolean result = line.deleteStationOfSection(deleteTarget);

        assertThat(result).isTrue();
    }
}