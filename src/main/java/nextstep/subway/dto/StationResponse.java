package nextstep.subway.dto;

import nextstep.subway.domain.LineStations;
import nextstep.subway.domain.Station;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        if (station == null) {
            return null;
        }
        return new StationResponse(station.getId(), station.getName());
    }

    public static List<StationResponse> of(LineStations lineStations) {
        if (lineStations.size() == 0) {
            return Collections.emptyList();
        }

        return lineStations.getSortedStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
