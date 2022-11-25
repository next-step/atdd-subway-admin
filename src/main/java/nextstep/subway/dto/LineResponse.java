package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int distance;
    private List<SectionResponse> sections = new ArrayList<>();
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static LineResponse of(Line line) {
        List<SectionResponse> sectionResponses = line.getSections().getSectionList()
                .stream()
                .map(section -> SectionResponse.of(section))
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getDistance(), sectionResponses, line.getCreatedDate(), line.getModifiedDate());
    }

    public LineResponse() {

    }

    public LineResponse(Long id, String name, String color, int distance, List<SectionResponse> sectionResponses, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.sections = sectionResponses;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return  color;
    }

    public int getDistance() {
        return distance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public List<SectionResponse> getSections() {
        return this.sections;
    }

    public LineResponse setSection(List<SectionResponse> sections) {
        this.sections = sections;
        return this;
    }
}
