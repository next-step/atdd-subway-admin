package nextstep.subway.line.dto;

import lombok.Getter;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<SectionResponse> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    protected LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<SectionResponse> stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        List<SectionResponse> sectionResponses = line.getLineSections().stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), sectionResponses, line.getCreatedDate(), line.getModifiedDate());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
