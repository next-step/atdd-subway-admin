package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LineResponse {
    private final long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        Set<StationResponse> stationResponses = new HashSet<>();
        Sections lineSections = line.getSections();

        lineSections.getSections().forEach(section -> {
            stationResponses.add(toStationResponse(section.getUpStation()));
            stationResponses.add(toStationResponse(section.getDownStation()));
        });

        List<StationResponse> responses = new ArrayList<>(stationResponses);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), responses);
    }

    private static StationResponse toStationResponse(Station station) {
        return StationResponse.of(station);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
