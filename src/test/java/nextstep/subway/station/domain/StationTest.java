package nextstep.subway.station.domain;

import nextstep.subway.station.exception.StationExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철역 클래스 테스트")
class StationTest {

    @Test
    void 동등성_테스트() {
        assertEquals(new Station("강남역"), new Station("강남역"));
    }

    @Test
    void Station_객체를_생성할때_name이_null이면_IllegalArgumentException_발생() {
        assertThatThrownBy(() -> {
            new Station(null);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(StationExceptionCode.REQUIRED_NAME.getMessage());
    }
}
