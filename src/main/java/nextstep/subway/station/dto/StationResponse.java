package nextstep.subway.station.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(), station.getModifiedDate());
    }
}
