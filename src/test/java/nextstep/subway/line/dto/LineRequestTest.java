package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineRequestTest {
    @Test
    void testToLine() {
        LineRequest request = new LineRequest("박달강남선", "blue");
        Line line = request.toLine();
        assertAll(
                () -> assertThat(line.getName()).isEqualTo(request.getName()),
                () -> assertThat(line.getColor()).isEqualTo(request.getColor())
        );
    }

    @Test
    void testHasSectionArgumentsFalse() {
        // given
        LineRequest request = new LineRequest("박달강남선", "blue");
        // when
        boolean hasNotSectionArguments = request.hasSectionArguments();
        // then
        assertThat(hasNotSectionArguments).isFalse();
    }

    @ParameterizedTest
    @CsvSource(value = {"1::100", ":2:100", "1:2:0"}, delimiter = ':')
    void testHasSectionArgumentsTrue(Long upStationId, Long downStationId, int distance) {
        // given
        LineRequest request = new LineRequest("박달강남선", "blue", upStationId, downStationId, distance);
        // when
        boolean hasNotSectionArguments = request.hasSectionArguments();
        // then
        assertThat(hasNotSectionArguments).isTrue();
    }
}
