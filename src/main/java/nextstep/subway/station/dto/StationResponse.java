package nextstep.subway.station.dto;

import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    private StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse from(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public static List<StationResponse> from(Sections sections) {
        List<StationResponse> stationResponses = new ArrayList<>();
        List<Station> stations = sections.getSortedStations();
        for (Station station : stations) {
            stationResponses.add(new StationResponse(station.getId(), station.getName()));
        }
        return stationResponses;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
