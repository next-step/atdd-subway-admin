package nextstep.subway.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import nextstep.subway.domain.Station;

public class StationResponse {

    private final Long id;
    private final String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private StationResponse(Long id, String name, LocalDateTime createdDate,
        LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(),
            station.getModifiedDate());
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StationResponse that = (StationResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
