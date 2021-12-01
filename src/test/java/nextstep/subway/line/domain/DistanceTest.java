package nextstep.subway.line.domain;

import static nextstep.subway.common.Message.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.Message;

class DistanceTest {

    @Test
    void 거리는_0보다_작을_수_없다() {
        // then
        Assertions.assertThatThrownBy(() -> {
                      Distance distance = new Distance(-1);
                  }).isInstanceOf(ArithmeticException.class)
                  .hasMessageStartingWith(Message.format(MESSAGE_SECTION_DISTANCE_NOT_LESS_THAN_ZERO, -1));
    }

    /**
     *  9 - 10 = -1 계산 에러
     */
    @Test
    void 현재거리에서_추가된_거리만큼_뺀다() {
        Distance distance = new Distance(9);
        Assertions.assertThatThrownBy(() -> {
            distance.subtract(new Distance(10));
        }).isInstanceOf(ArithmeticException.class);
    }

    @Test
    void 현재거리보다_작은_수를_입력시_정상() {
        Distance distance = new Distance(9);
        Distance result = distance.subtract(new Distance(5));

        Assertions.assertThat(result).isEqualTo(new Distance(4));
    }
}