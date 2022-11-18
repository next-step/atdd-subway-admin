package nextstep.subway.line.domain;

import nextstep.subway.line.exception.SectionExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("거리 정보 클래스 테스트")
class DistanceTest {

    @ParameterizedTest
    @ValueSource(ints = { -1, -10, -20, -30 })
    void 거리_객체_생성시_거리값이_음수이면_IllegalArgumentException_발생(int distance) {
        assertThatThrownBy(() -> {
            new Distance(distance);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SectionExceptionCode.DO_NOT_ALLOW_NEGATIVE_NUMBER_DISTANCE.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = { "10:20", "20:50", "30:40" }, delimiter = ':')
    void 거리_차감시_입력된_거리값이_현재_거리값보다_크거나_같으면_IllegalArgumentException_발생(int current, int request) {
        assertThatThrownBy(() -> {
            Distance distance = new Distance(current);
            distance.minus(request);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SectionExceptionCode.INVALID_DISTANCE.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = { "20:10", "50:20", "10:5" }, delimiter = ':')
    void 거리_차감(int current, int request) {
        Distance distance = new Distance(current);
        distance = distance.minus(request);
        assertEquals((current - request), distance.getDistance());
    }

    @ParameterizedTest
    @CsvSource(value = { "10:10", "10:20", "20:30" }, delimiter = ':')
    void 거리_가산(int current, int request) {
        Distance distance = new Distance(current);
        distance = distance.plus(request);
        assertEquals((current + request), distance.getDistance());
    }
}
