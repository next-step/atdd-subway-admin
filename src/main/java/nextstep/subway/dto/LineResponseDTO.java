package nextstep.subway.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.collection.Stations;

public class LineResponseDTO {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public static LineResponseDTO of(Line line) {
        List<StationResponse> stationResponses = new ArrayList<>();
        Stations stations = line.getStations();
        for (Station station : stations.getStations()) {
            stationResponses.add(StationResponse.of(station));
        }
        return new LineResponseDTO(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    private LineResponseDTO(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Long getId() {
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

    public static class StationResponse {
        private final Long id;
        private final String name;
        private final LocalDateTime createdDate;
        private final LocalDateTime modifiedDate;

        public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
            this.id = id;
            this.name = name;
            this.createdDate = createdDate;
            this.modifiedDate = modifiedDate;
        }

        public static StationResponse of(Station station) {
            return new StationResponse(station.getId()
                    , station.getName()
                    , station.getCreatedDate()
                    , station.getModifiedDate());
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
