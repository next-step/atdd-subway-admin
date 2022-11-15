package nextstep.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineResponse {
    public Long id;
    public String name;
    public String color;
    public List<StationResponse> stations;
    public int distance;


   public static List<LineResponse> of(List<Line> lines) {
       LineResponse.builder().build();

       return lines.stream()
               .map(line -> LineResponse.of(line))
               .collect(Collectors.toList());
   }

    public static LineResponse of(Line persistLine) {
        return new LineResponse(persistLine);
    }

    private LineResponse (Line line) {
        id = line.getId();
        name = line.getName();
        color = line.getColor();
        stations = line.getStations().stream().map(StationResponse::of).collect(Collectors.toList());
    }
}
