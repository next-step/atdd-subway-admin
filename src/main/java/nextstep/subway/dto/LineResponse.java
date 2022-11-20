package nextstep.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;

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
    public int totalDistance;



    public static List<LineResponse> of(List<Line> lines) {
        return lines.stream()
                .map(line -> LineResponse.from(line))
                .collect(Collectors.toList());
    }

    public static LineResponse from(Line persistLine) {
        return new LineResponse(persistLine);
    }

    private LineResponse (Line line) {
        id = line.getId();
        name = line.getName();
        color = line.getColor();
        stations = line.getStations().stream().map(StationResponse::of).collect(Collectors.toList());
        totalDistance = line.getSections().stream().mapToInt(s -> s.getDistance()).sum();
    }

    @Data
    public static class Section implements Comparable<Section> {
        private Long id;
        private Long upStationId;
        private Long downStationId;
        private int distance;

        private Section(Long id, Long upStationId, Long downStationId, int distance) {
            this.id = id;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        public static Section of(Long id, Long upStationId, Long downStationId, int distance) {
            return new Section(id, upStationId, downStationId, distance);
        }

        @Override
        public int compareTo(Section s) {
            if (this.downStationId.equals(s.getUpStationId())) return -1;
            else if (this.upStationId.equals(s.getDownStationId())) return 1;
            return 0;
        }
    }
}
