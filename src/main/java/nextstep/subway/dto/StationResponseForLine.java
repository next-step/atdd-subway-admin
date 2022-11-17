package nextstep.subway.dto;

import nextstep.subway.domain.Station;

public class StationResponseForLine {
    private final Long id;
    private final String name;

    private StationResponseForLine(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public static StationResponseForLine of(Station station) {
        return new StationResponseForLine(station);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
