package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private List<SectionResponse> sections;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate,
            List<SectionResponse> sections, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.sections = sections;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return Builder.LineResponse()
            .id(line.getId())
            .name(line.getName())
            .color(line.getColor())
            .sections(line.getSections().stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList()))
            .stations(line.getStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList()))
            .createdDate(line.getCreatedDate())
            .modifiedDate(line.getModifiedDate())
            .build();
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

    public List<StationResponse> getStations() {
        return stations;
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String color;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
        private List<SectionResponse> sections;
        private List<StationResponse> stations;

        private Builder() {
        }

        public static Builder LineResponse() {
            return new Builder();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder createdDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder modifiedDate(LocalDateTime modifiedDate) {
            this.modifiedDate = modifiedDate;
            return this;
        }

        public Builder sections(List<SectionResponse> sections) {
            this.sections = sections;
            return this;
        }

        public Builder stations(List<StationResponse> stations) {
            this.stations = stations;
            return this;
        }

        public LineResponse build() {
            return new LineResponse(id, name, color, createdDate, modifiedDate, sections, stations);
        }
    }
}
