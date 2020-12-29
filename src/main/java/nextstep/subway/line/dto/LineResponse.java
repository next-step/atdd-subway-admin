package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    private LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate()
                , line.getModifiedDate(), toStations(line.getSections()));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineResponse that = (LineResponse) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(color, that.color)) return false;
        if (!Objects.equals(createdDate, that.createdDate)) return false;
        return Objects.equals(modifiedDate, that.modifiedDate);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (modifiedDate != null ? modifiedDate.hashCode() : 0);
        return result;
    }

    private static List<StationResponse> toStations(List<Section> sections) {
        List<Section> sortedSections = makeSortedSections(sections);
        return makeStationResponses(sortedSections);
    }

    private static List<Section> makeSortedSections(List<Section> sections) {
        return sections.stream()
                .sorted(Comparator.comparing(Section::getSectionNumber))
                .collect(Collectors.toList());
    }

    private static List<StationResponse> makeStationResponses(List<Section> sections) {
        List<StationResponse> stations = sections.stream()
                .map(Section::getUpStation)
                .map(StationResponse::of)
                .collect(Collectors.toList());

        int lastIdx = sections.size() - 1;
        Section lastSection = sections.get(lastIdx);
        stations.add(StationResponse.of(lastSection.getDownStation()));
        return stations;
    }
}
