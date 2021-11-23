package nextstep.subway.line.dto;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionRequestTest {
    @Test
    void testOfLineRequest() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "불루", 1L, 2L, 100);
        // when
        SectionRequest sectionRequest = SectionRequest.of(lineRequest);
        // then
        assertAll(
                () -> assertThat(sectionRequest.getUpStationId()).isEqualTo(lineRequest.getUpStationId()) ,
                () -> assertThat(sectionRequest.getDownStationId()).isEqualTo(lineRequest.getDownStationId()) ,
                () -> assertThat(sectionRequest.getDistance()).isEqualTo(lineRequest.getDistance())
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"1::100", ":2:100", "1:2:0"}, delimiter = ':')
    void testOfLineRequestException(Long upStationId, Long downStationId, int distance) {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "불루", upStationId, downStationId, distance);
        // when
        ThrowableAssert.ThrowingCallable throwable = () -> SectionRequest.of(lineRequest);
        // then
        assertThatThrownBy(throwable)
                .isInstanceOf(IllegalArgumentException.class);
    }
}
