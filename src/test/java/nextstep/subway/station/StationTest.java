package nextstep.subway.station;

import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StationTest {

    @DisplayName("지하철역 생성 테스트")
    @Test
    void createStationTest() {
        Station station = new Station("강남역");
        assertThat(station.getName()).isEqualTo("강남역");
    }
}
