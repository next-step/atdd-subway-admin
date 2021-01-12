package nextstep.subway.line.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static LineResponse of(Line line) {
        Set<StationResponse> stationSet = new TreeSet<>();
        for (Section section : line.getSections().getSections()) {
            stationSet.add(StationResponse.of(section.getUpStation()));
            stationSet.add(StationResponse.of(section.getDownStation()));
        }
        return new LineResponse(line.getId(), line.getName(), line.getColor(), new ArrayList<>(stationSet), line.getCreatedDate(), line.getModifiedDate());
    }

    public static List<LineResponse> ofList(List<Line> lines) {
        return lines.stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }
}
