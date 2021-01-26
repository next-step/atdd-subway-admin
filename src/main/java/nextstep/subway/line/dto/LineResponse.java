package nextstep.subway.line.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Getter
@NoArgsConstructor
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        List<StationResponse> stationResponses = CollectionUtils.emptyIfNull(line.getAllSection()).stream()
                .map(section -> {
                    Station upStation = section.getUpStation();
                    Station downStation = section.getDownStation();
                    upStation.updateDistance(section.getDistance());
                    return Arrays.asList(
                            upStation,
                            downStation
                    );
                })
                .flatMap(Collection::stream)
                .distinct()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses, line.getCreatedDate(), line.getModifiedDate());
    }
}
