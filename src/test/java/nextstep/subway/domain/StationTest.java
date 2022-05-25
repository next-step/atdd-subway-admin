package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @Test
    @DisplayName("두 객체가 같은지 검증")
    void verifySameStationObject() {
        assertThat(new Station("강남역")).isEqualTo(new Station("강남역"));
    }
}
