package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;

public class LineResponse {

    private Long id;

    private String name;

    private String color;

    private List<SectionResponse> sections;

    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(final Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.sections = line.getSections().toList().stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
        this.stations = line.getSections().getSortedStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public static LineResponse of(final Line line) {
        return new LineResponse(line);
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

    public List<SectionResponse> getSections() {
        return sections;
    }
}
