package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.subway.common.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DistanceTest {
    @DisplayName("정상 생성 테스트")
    @Test
    void create() {
        //given
        //when
        Distance distance = new Distance(7);
        //then
        assertThat(distance).isNotNull();
    }

    @DisplayName("거리 빼기")
    @Test
    void sub() {
        //given
        Distance distance = new Distance(7);
        //when
        Distance add = distance.sub(new Distance(4));
        //then
        assertThat(add.equals(new Distance(3))).isTrue();
    }

    @DisplayName("거리 더하기")
    @Test
    void add() {
        //given
        Distance distance = new Distance(7);
        //when
        Distance add = distance.add(new Distance(3));
        //then
        assertThat(add.equals(new Distance(10))).isTrue();
    }

    @DisplayName("음수 에러")
    @Test
    void createFailed() {
        //given
        //when
        //then
        assertThatThrownBy(() -> {
            new Distance(-1);
        }).isInstanceOf(RuntimeException.class)
                .hasMessage(ErrorMessage.DISTANCE_TOO_LONG);
    }
}