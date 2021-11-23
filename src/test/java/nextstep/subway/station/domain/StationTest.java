package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class StationTest {

    @Test
    void 이름으로_지하철역을_만들수_있다() {
        //given
        final String name = "강남";

        //when
        final Station station = 지하철역_생성(name);

        //then
        지하철역_생성됨(name, station);
    }

    public static Station 지하철역_생성(final String name) {
        return new Station(name);
    }

    public static Station 지하철역_생성_및_검증(final String name) {
        final Station station = 지하철역_생성(name);

        지하철역_생성됨(name, station);

        return station;
    }

    public static void 지하철역_생성됨(final String name, final Station station) {
        assertThat(station).isEqualTo(지하철역_생성(name));
    }
}