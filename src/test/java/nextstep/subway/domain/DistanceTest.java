package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.params.ParameterizedTest.DEFAULT_DISPLAY_NAME;

@DisplayName("거리 테스트")
class DistanceTest {

    @DisplayName("생성 성공")
    @Test
    void create_distance_success() {
        int num = 1;
        Distance distance = new Distance(num);
        assertThat(distance.getDistance()).isEqualTo(num);
    }

    @ParameterizedTest(name = "생성 실패 - 유효하지 않은 거리" + DEFAULT_DISPLAY_NAME)
    @ValueSource(ints = { 0, -1 })
    void create_distance_success(int num) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Distance(num));
    }

    @DisplayName("minus 메서드 테스트")
    @Test
    void minus_distance_success() {
        int source = 10;
        int minus = 6;
        int expect = 4;
        Distance sourceDistance = new Distance(source);
        Distance minusDistance = new Distance(minus);
        assertThat(sourceDistance.minus(minusDistance)).isEqualTo(new Distance(expect));
    }

    @DisplayName("minus 메서드 테스트 실패 - 기존 거리보다 큰 값 뺄셈을 시도할 경우")
    @Test
    void minus_distance_IllegalArgumentException() {
        int source = 10;
        int minus = 11;
        Distance sourceDistance = new Distance(source);
        Distance minusDistance = new Distance(minus);
        assertThatIllegalArgumentException().isThrownBy(() -> sourceDistance.minus(minusDistance));
    }
}
