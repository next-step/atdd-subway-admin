package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class SectionResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final int distance;
    private final List<SectionDto> sections;

    public SectionResponse(Long id, String name, String color, int distance, List<SectionDto> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.sections = sections;
    }

    public static SectionResponse from(Line line) {
        List<SectionDto> sectionDtos = line.getSections().getSections().stream()
                .map(section -> SectionDto.of(section.getDownStation().getName().getName()
                        , section.getUpStation().getName().getName()
                        , section.getDistance().getDistance()))
                .collect(Collectors.toList());

        return new SectionResponse(line.getId(), line.getName().getName(), line.getColor().getColor(), line.getDistance().getDistance(), sectionDtos);
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

    public int getDistance() {
        return distance;
    }

    public List<SectionDto> getSections() {
        return sections;
    }

    static class SectionDto {
        private final String downStation;
        private final String upStation;
        private final int distance;

        public SectionDto(String downStation, String upStation, int distance) {
            this.downStation = downStation;
            this.upStation = upStation;
            this.distance = distance;
        }

        public static SectionDto of(String downStation, String upStation, int distance) {
            return new SectionDto(downStation, upStation, distance);
        }

        public String getDownStation() {
            return downStation;
        }

        public String getUpStation() {
            return upStation;
        }

        public int getDistance() {
            return distance;
        }
    }

}
