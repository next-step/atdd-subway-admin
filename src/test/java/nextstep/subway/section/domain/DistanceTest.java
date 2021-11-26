package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DistanceTest {

    @Test
    void 숫자를_입력해_지하철_노선_구간거리를_만들_수있다() {
        //given
        final int distanceNumber = 7;

        //when
        final Distance distance = 지하철_노선_구간거리_생성(distanceNumber);

        //then
        지하철_노선_구간거리_생성됨(distance, distanceNumber);
    }

    public static Distance 지하철_노선_구간거리_생성(final int distanceNumber) {
        return new Distance(distanceNumber);
    }

    public static Distance 지하철_노선_구간거리_생성_및_검증(final int distanceNumber) {
        final Distance distance = 지하철_노선_구간거리_생성(distanceNumber);

        지하철_노선_구간거리_생성됨(distance, distanceNumber);

        return distance;
    }

    public static void 지하철_노선_구간거리_생성됨(final Distance distance, final int distanceNumber) {
        assertThat(distance).isEqualTo(new Distance(distanceNumber));
    }
}