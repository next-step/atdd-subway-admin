package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final Integer distance;
    private final List<StationResponse> finalStations;
    private final List<SectionResponse> sections;

    private LineResponse(Long id, String name, String color, Integer distance, List<StationResponse> finalStations, List<SectionResponse> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.finalStations = finalStations;
        this.sections = sections;
    }

    public static LineResponse of(Line line) {
        List<StationResponse> finalStations = Arrays.asList(
                StationResponse.of(line.getUpFinalStation()),
                StationResponse.of(line.getDownFinalStation())
        );
        List<SectionResponse> sections = new ArrayList<>();
        line.getAllSections().forEach(section -> sections.add(SectionResponse.of(section)));

        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getDistance(), finalStations, sections);
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

    public Integer getDistance() {
        return distance;
    }

    public List<StationResponse> getFinalStations() {
        return finalStations;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }
}
