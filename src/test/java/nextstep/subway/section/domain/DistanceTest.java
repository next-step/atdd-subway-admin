package nextstep.subway.section.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("거리 관련 기능")
public class DistanceTest {
    @DisplayName("거리를 정상 생성한다.")
    @Test
    void create() {
        // given
        Integer distanceValue = 10;

        // when
        Distance createdDistance = Distance.valueOf(distanceValue);

        // then
        assertAll("validateValue",
            () -> Assertions.assertThat(createdDistance.value()).isEqualTo(distanceValue)
        );
    }

    @DisplayName("거리를 음수로 입력시 에러가 발생한다.")
    @Test
    void validate_negativeValue() {
        // given
        Integer distanceValue = -10;

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> Distance.valueOf(distanceValue));
    }

    @DisplayName("거리를 0으로 입력시 에러가 발생한다.")
    @Test
    void validate_zeroValue() {
        // given
        Integer distanceValue = 0;

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> Distance.valueOf(distanceValue));
    }
}
