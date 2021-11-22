package nextstep.subway.line.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Distance 테스트")
class DistanceTest {

    @Nested
    @DisplayName("생성 확인 테스트")
    class 생성_확인_테스트 {
        @DisplayName("생성 확인")
        @ParameterizedTest(name = "{displayName} ({index}) -> param = [{arguments}]")
        @ValueSource(ints = {1, 10, 100})
        void 생성_확인(int testValue) {
            // when
            Distance distance = Distance.of(testValue);

            // then
            assertThat(distance)
                    .isNotNull();
        }
    }

    @Nested
    @DisplayName("생성 실패 테스트")
    class 생성_실패_테스트 {
        @DisplayName("거리 값이 0이하")
        @ParameterizedTest(name = "{displayName} ({index}) -> param = [{arguments}]")
        @ValueSource(ints = {0, -1, -10, -100})
        void 거리_값이_0이하(int testValue) {
            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> Distance.of(testValue);

            // then
            assertThatThrownBy(throwingCallable)
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}