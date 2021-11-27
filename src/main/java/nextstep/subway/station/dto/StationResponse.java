package nextstep.subway.station.dto;

import nextstep.subway.common.exception.NotFoundStationException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class StationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public StationResponse() {
    }

    public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(), station.getModifiedDate());
    }

    public static List<StationResponse> listOf(List<Station> stations) {
            return stations.stream()
                    .map(StationResponse::of)
                    .collect(Collectors.toList());
    }

    public static List<StationResponse> listFromSectionsOf(List<Section> sections) {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        return getSortedStations(sections);
    }

    private static List<StationResponse> getSortedStations(List<Section> sections) {
        Set<StationResponse> stationResponses = new LinkedHashSet<>();
        Section currentSection = findFirstSection(sections);
        Section endSection = findLastSection(sections);

        while (!currentSection.equals(endSection)) {
            addStations(stationResponses, currentSection);
            currentSection = findNextSection(sections, currentSection);
        }
        addStations(stationResponses, endSection);

        return new ArrayList<>(stationResponses);
    }

    private static void addStations(Set<StationResponse> stationResponses, Section currentSection) {
        stationResponses.add(StationResponse.of(currentSection.getUpStation()));
        stationResponses.add(StationResponse.of(currentSection.getDownStation()));
    }

    private static Section findFirstSection(List<Section> sections) {
        Set<StationResponse> downStations = sections.stream()
                .map(section -> StationResponse.of(section.getDownStation()))
                .collect(Collectors.toCollection(HashSet::new));

        return sections.stream()
                .filter(section -> !downStations.contains(StationResponse.of(section.getUpStation())))
                .findAny()
                .orElseThrow(NotFoundStationException::new);
    }

    private static Section findLastSection(List<Section> sections) {
        Set<StationResponse> upStations = sections.stream()
                .map(section -> StationResponse.of(section.getUpStation()))
                .collect(Collectors.toCollection(HashSet::new));

        return sections.stream()
                .filter(section -> !upStations.contains(StationResponse.of(section.getDownStation())))
                .findAny()
                .orElseThrow(NotFoundStationException::new);
    }

    private static Section findNextSection(List<Section> sections, Section currentSection) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(currentSection.getDownStation()))
                .findAny()
                .orElseThrow(NotFoundStationException::new);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationResponse that = (StationResponse) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
