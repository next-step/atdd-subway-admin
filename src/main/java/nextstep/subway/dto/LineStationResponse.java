package nextstep.subway.dto;

import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStations;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineStationResponse {
    private final Long id;
    private final String name;

    public LineStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<LineStationResponse> of(LineStations lineStations) {
        if (lineStations.size() == 0) {
            return Collections.emptyList();
        }

        List<LineStationResponse> result = new ArrayList<>();

        result.add(of(lineStations.getFirst().getUpStation()));
        for (LineStation lineStation : lineStations.getLineStations()) {
            result.add(of(lineStation.getDownStation()));
        }
        return result;
    }

    public static LineStationResponse of(Station station) {
        return new LineStationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
