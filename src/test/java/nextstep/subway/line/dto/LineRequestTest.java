package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineRequestTest {
    @DisplayName("Line 객체을 생성한다")
    @Test
    void testToLine() {
        // given
        LineRequest request = new LineRequest("박달강남선", "blue");
        // when
        Line line = request.toLine();
        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo(request.getName()),
                () -> assertThat(line.getColor()).isEqualTo(request.getColor())
        );
    }

    @DisplayName("구간(상행선, 하행선, 거리) 값이 모두 없으면 false를 반환한다")
    @Test
    void testHasSectionArgumentsFalse() {
        // given
        LineRequest request = new LineRequest("박달강남선", "blue");
        // when
        boolean hasNotSectionArguments = request.hasSectionArguments();
        // then
        assertThat(hasNotSectionArguments).isFalse();
    }

    @DisplayName("구간(상행선, 하행선, 거리) 값이 하나라도 있으면 true를 반환한다")
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

    @DisplayName("SectionRequest 객체를 생성한다")
    @Test
    void testToSectionRequest() {
        // given
        LineRequest request = new LineRequest("박달강남선", "blue", 1L, 2L, 191);
        // when
        SectionRequest sectionRequest = request.toSectionRequest();
        // then
        assertAll(
                () -> assertThat(sectionRequest.getUpStationId()).isEqualTo(request.getUpStationId()),
                () -> assertThat(sectionRequest.getDownStationId()).isEqualTo(request.getDownStationId()),
                () -> assertThat(sectionRequest.getDistance()).isEqualTo(request.getDistance())
        );
    }
}
