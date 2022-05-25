package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertEquals;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StationTest {

    @DisplayName("지하철 역 도메인을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"서울대입구역", "판교역", "수서역", "압구정역"})
    void generate01(String name) {
        // given & when
        Station station = Station.from(name);

        // then
        assertEquals(StationName.from(name).getName(), station.getName());
    }

    @DisplayName("지하철 역 명이 null 이나 빈 문자열일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void generate02(String name) {
        // given & then & then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Station.from(name))
            .withMessageMatching("지하철 역 명이 비어있습니다.");
    }
}