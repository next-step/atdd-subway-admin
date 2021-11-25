package nextstep.subway.station.dto;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;

public class StationResponse {

    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(),
            station.getModifiedDate());
    }

    public StationResponse() {
    }

    public StationResponse(Long id, String name, LocalDateTime createdDate,
        LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static List<StationResponse> listOf(List<Station> stations) {
        List<StationResponse> result = new ArrayList<>();
        for (Station station : stations) {
            result.add(StationResponse.of(station));
        }
        return result;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
