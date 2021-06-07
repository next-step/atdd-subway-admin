package nextstep.subway.station;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Station 클래스 관련 테스트")
public class StationTest {

    @Test
    void create() {
        String name= "지하철역";
        Station station = Station.of(name);

        assertThat(station).isNotNull();
        assertThat(station.getName()).isEqualTo(name);
    }

}
