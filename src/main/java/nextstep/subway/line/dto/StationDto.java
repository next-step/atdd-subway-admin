package nextstep.subway.line.dto;

import nextstep.subway.station.domain.Station;

class StationDto {
    private final Long id;
    private final String name;

    private StationDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationDto from(Station station) {
        return new StationDto(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
