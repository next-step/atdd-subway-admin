package nextstep.subway.line.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    public LineResponse(final Long id, final String name,
                        final String color, final List<StationResponse> stations,
                        final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .createdDate(line.getCreatedDate())
                .modifiedDate(line.getModifiedDate())
                .build();
    }

    public static List<LineResponse> ofList(final List<Line> lines) {
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Getter
    public static class StationResponse {

        private final Long id;
        private final String name;
        private final LocalDateTime createdDate;
        private final LocalDateTime modifiedDate;

        @Builder
        private StationResponse(final Long id, final String name,
                                final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
            this.id = id;
            this.name = name;
            this.createdDate = createdDate;
            this.modifiedDate = modifiedDate;
        }
    }
}
