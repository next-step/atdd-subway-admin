package nextstep.subway.dto;

import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StationResponse {
    private Long id;
    private String name;

    public static StationResponse from(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<StationResponse> from(Sections sections) {
        List<StationResponse> result = new ArrayList<>();
        List<Station> stations = sections.getSortedStations();
        for (Station station : stations) {
            result.add(new StationResponse(station.getId(), station.getName()));
        }
        return result;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
