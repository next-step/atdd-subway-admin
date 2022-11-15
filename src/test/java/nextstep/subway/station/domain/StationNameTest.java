package nextstep.subway.station.domain;

import nextstep.subway.common.message.ExceptionMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StationNameTest {

    @DisplayName("지하철역 이름 값이 null 이거나 empty 이면 StationName 객체 생성 시 예외가 발생한다.")
    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @NullAndEmptySource
    void nullOrEmptyName(String input) {
        Assertions.assertThatThrownBy(() -> StationName.from(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith(ExceptionMessage.REQUIRED);
    }

    @DisplayName("지하철역 이름 값이 null 과 empty 가 아니면 StationName 객체가 정상적으로 생성된다.")
    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @ValueSource(strings = {"강남역", "신사역", "광교역"})
    void createStationName(String input) {
        StationName name = StationName.from(input);

        Assertions.assertThat(name).isNotNull();
    }

    @DisplayName("지하철역 이름 값이 다른 StationName 객체는 동등하지 않다.")
    @Test
    void equalsFail() {
        StationName name1 = StationName.from("광교역");
        StationName name2 = StationName.from("강남역");

        Assertions.assertThat(name1).isNotEqualTo(name2);
    }

    @DisplayName("지하철역 이름 값이 같은 StationName 객체는 동등하다.")
    @Test
    void equalsSuccess() {
        StationName name1 = StationName.from("강남역");
        StationName name2 = StationName.from("강남역");

        Assertions.assertThat(name1).isEqualTo(name2);
    }
}
