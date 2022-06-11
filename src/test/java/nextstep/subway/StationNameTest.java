package nextstep.subway;

import nextstep.subway.domain.StationName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StationNameTest {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("지하철역 이름 값 비어있을 경우 Exception 발생 확인")
    void validate_empty(String name) {
        assertThatThrownBy(() -> {
            new StationName(name);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"잠실역", "강남역"})
    @DisplayName("지하철역 이름 생성후 값 비교 확인")
    void validateStringNumber(String expected) {
        StationName stationName = new StationName(expected);
        assertThat(stationName.getName()).isEqualTo(expected);
    }
}
