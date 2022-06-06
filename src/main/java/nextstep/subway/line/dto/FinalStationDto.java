package nextstep.subway.line.dto;

import nextstep.subway.station.Station;

class FinalStationDto {
    private final Long id;
    private final String name;

    private FinalStationDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static FinalStationDto from(Station station) {
        return new FinalStationDto(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
