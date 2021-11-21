package nextstep.subway.station.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import nextstep.subway.station.domain.Station;

public class StationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static List<StationResponse> of(final List<Station> stations) {
        return stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    public static StationResponse of(final Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(),
            station.getModifiedDate());
    }

    public StationResponse(final Long id, final String name, final LocalDateTime createdDate,
        final LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
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
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final StationResponse that = (StationResponse)obj;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
            && Objects.equals(createdDate, that.createdDate) && Objects.equals(modifiedDate,
            that.modifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdDate, modifiedDate);
    }
}
