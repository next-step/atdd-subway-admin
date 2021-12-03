package nextstep.subway.line.domain;

import nextstep.subway.exception.BadRequestException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("구간의 거리 관련 기능")
class DistanceTest {

    @Test
    void 거리를_생성() {
        // given-when
        Distance distance = Distance.from(10);

        // then
        Assertions.assertThat(distance.value()).isEqualTo(10);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 유효한_거리가_아닐_경우_생성_실패(int input) {
        // given-when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Distance.from(input);

        // then
        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(throwingCallable);
    }
}
