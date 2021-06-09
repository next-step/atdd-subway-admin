package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
