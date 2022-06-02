package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<SectionResponse> sections;

    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public static LineResponse of(Line line) {
        List<SectionResponse> sectionResList = line.getSections().getSections().stream().map(SectionResponse::of).collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), sectionResList, line.getCreatedDate(), line.getModifiedDate());
    }

    public LineResponse(Long id, String name, String color, List<SectionResponse> sections, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
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
        return color;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }
}
