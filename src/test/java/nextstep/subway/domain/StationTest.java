package nextstep.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class StationTest {

    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @NullAndEmptySource
    @DisplayName("지하철역 생성 시 이름이 없으면 예외를 발생한다.")
    void createStation(String input) {
        Assertions.assertThatThrownBy(() -> Station.from(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("지하철역이 정상적으로 생성된다.")
    void createStation2() {
        Station station = Station.from("신사역");
        Assertions.assertThat(station).isNotNull();
    }
}