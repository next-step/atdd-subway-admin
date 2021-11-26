package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DistanceTest {

    @Test
    void 숫자를_입력해_지하철_노선_구간거리를_만들_수있다() {
        //given
        final int distanceNumber = 7;

        //when
        final Distance distance = new Distance(distanceNumber);

        //then
        assertThat(distance).isEqualTo(new Distance(distanceNumber));
    }
}