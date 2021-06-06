package nextstep.subway.station;

import nextstep.subway.station.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class StationHelper {

    public static Station 가짜_역_생성(String name, Long id) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}
