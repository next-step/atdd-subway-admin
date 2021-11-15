package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StationTest {

    @DisplayName("이름 정보로 Station 을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"판교역", "정자역", "미금역"})
    void create1(String name) {
        // when
        Station station = Station.from(name);

        // then
        assertEquals(station.getName(), StationName.from(name));
    }

    @DisplayName("Station 이름을 null or 빈 문자열로 할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create2(String name) {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Station.from(name))
                                            .withMessageContaining("역 이름이 비어있습니다.");
    }
}