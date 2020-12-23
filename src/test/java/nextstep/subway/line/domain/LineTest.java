package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.EndUpStationNotFoundException;
import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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
}