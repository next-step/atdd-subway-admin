package nextstep.subway.line.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @Nested
    @DisplayName("거리 비교 테스트")
    class 거리_비교_테스트 {
        @DisplayName("비교 대상 거리가 미만값")
        @ParameterizedTest(name = "{displayName} ({index}) -> param = [{arguments}]")
        @CsvSource(value = {"10:9", "10:1"}, delimiter = ':')
        void 비교_대상_거리가_이하값(int 기준값, int 대상값) {
            // given
            Distance 기준 = Distance.of(기준값);
            Distance 대상 = Distance.of(대상값);

            // when
            boolean result = 기준.isGreaterThan(대상);

            // then
            assertThat(result)
                    .isFalse();
        }

        @DisplayName("비교 대상 거리가 이상값")
        @ParameterizedTest(name = "{displayName} ({index}) -> param = [{arguments}]")
        @CsvSource(value = {"10:10", "10:11", "10:100"}, delimiter = ':')
        void 비교_대상_거리가_이상값(int 기준값, int 대상값) {
            // given
            Distance 기준 = Distance.of(기준값);
            Distance 대상 = Distance.of(대상값);

            // when
            boolean result = 기준.isGreaterThan(대상);

            // then
            assertThat(result)
                    .isTrue();
        }
    }
}