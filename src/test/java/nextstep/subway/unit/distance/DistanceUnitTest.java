package nextstep.subway.unit.distance;

import nextstep.subway.application.exception.exception.NotFoundDataException;
import nextstep.subway.application.exception.exception.NotValidDataException;
import nextstep.subway.domain.Distance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static nextstep.subway.application.exception.type.AlreadyExceptionType.ALREADY_LINE_STATION;
import static nextstep.subway.application.exception.type.ValidExceptionType.NOT_VALID_DISTANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class DistanceUnitTest {

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("거리가 0보다 작거나 같으면 예외를 발생시킨다")
    void distanceIfMinZeroException(int distance) {
        assertThatThrownBy(() -> {
            new Distance(distance);
        }).isInstanceOf(NotValidDataException.class)
                .hasMessageContaining(NOT_VALID_DISTANCE.getMessage());
    }


    @Test
    @DisplayName("유입받은 숫자로 부터 갖고있는 거리를 뺀다")
    void distanceMinus() {
        Distance distance = new Distance(10);
        Distance minusDistance = distance.minus(new Distance(2));

        assertThat(minusDistance.getDistance()).isEqualTo(8);
    }
}