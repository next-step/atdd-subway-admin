package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class StationTest {

    @Test
    void 이름으로_지하철역을_만들수_있다() {
        //given
        final String name = "강남";

        //when
        final Station station = new Station(name);

        //then
        assertThat(station).isEqualTo(new Station(name));
    }
}