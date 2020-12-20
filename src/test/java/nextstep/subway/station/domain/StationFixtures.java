package nextstep.subway.station.domain;

import java.time.LocalDateTime;

public class StationFixtures {
    public final static Station ID1_STATION = new Station(1L, "갱남역");
    public final static Station ID2_STATION = new Station(2L, "서초역");

    public static Station createStationFixture(final Long id, final String name, final LocalDateTime createdAt) {
        Station station = new Station(id, name);
        station.updateCreatedDate(createdAt);

        return station;
    }
}
