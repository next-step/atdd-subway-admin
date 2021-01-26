package nextstep.subway.station.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class StationResponse {
    private Long id;
    private String name;
    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getDistance(), station.getCreatedDate(), station.getModifiedDate());
    }

    public StationResponse(Long id, String name, int distance, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
