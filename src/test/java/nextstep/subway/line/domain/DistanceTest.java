package nextstep.subway.line.domain;

import nextstep.subway.line.domain.Distance;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @Test
    void create() {
        //given
        Distance distance = new Distance(1);

        //when
        int actual = distance.getDistance();

        //then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void createInvalidException() {
        //when
        assertThatThrownBy(() -> new Distance(-1))
                .isInstanceOf(IllegalArgumentException.class) //then
                .hasMessage(Distance.INVALID_DISTANCE_EXCEPTION_MESSAGE);
    }
}