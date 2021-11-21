package nextstep.subway.line.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * packageName : nextstep.subway.line.dto
 * fileName : DistanceTest
 * author : haedoang
 * date : 2021/11/21
 * description :
 */
class DistanceTest {

    @Test
    @DisplayName("거리를 생성한다.")
    public void create() {
        //given
        int distance = 1;

        //when
        Distance given = Distance.of(distance);

        //then
        assertThat(given).isEqualTo(Distance.of(1));
    }

    @ParameterizedTest(name = "유효하지 않은 Distance : " + ParameterizedTest.ARGUMENTS_PLACEHOLDER)
    @ValueSource(ints = {-99, -1, 0, Distance.MIN_DISTANCE -1})
    public void invalidate(int candidate) {
        //then
        assertThatThrownBy(() -> Distance.of(candidate)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리는 1보다 작을 수 없습니다.");
    }

}