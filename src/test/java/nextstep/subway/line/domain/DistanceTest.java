package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @Test
    void create() {
        //given
        Distance distance = Distance.valueOf(1);

        //when
        int actual = distance.getDistance();

        //then
        assertThat(actual).isEqualTo(1);
    }

    @DisplayName("거리는 음수가 될 수 없다.")
    @Test
    void createInvalidException() {
        //when
        assertThatThrownBy(() -> Distance.valueOf(-1))
                .isInstanceOf(IllegalArgumentException.class) //then
                .hasMessage(Distance.INVALID_DISTANCE_EXCEPTION_MESSAGE);
    }

    @DisplayName("기존 거리를 요청한 거리 만큼 뺀다.")
    @Test
    void minus() {
        //when
        Distance actual = Distance.valueOf(10).minus(Distance.valueOf(3));

        //then
        assertThat(actual).isEqualTo(Distance.valueOf(7));
    }

    @DisplayName("기존 거리보다 큰 거리를 뺄 수 없다.")
    @Test
    void isAvailableMinus() {
        //when
        boolean actual = Distance.valueOf(10).isAvailableMinus(Distance.valueOf(11));

        //then
        assertThat(actual).isFalse();
    }
}