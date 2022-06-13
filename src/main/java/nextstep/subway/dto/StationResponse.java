package nextstep.subway.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import nextstep.subway.domain.Station;

public class StationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public StationResponse() {
    }

    public StationResponse(final Long id,
                           final String name,
                           final LocalDateTime createdDate,
                           final LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static StationResponse of(final Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCreatedDate(),
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
    public String toString() {
        return "StationResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdDate=" + createdDate +
                ", modifiedDate=" + modifiedDate +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StationResponse that = (StationResponse) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(createdDate, that.createdDate)
                && Objects.equals(modifiedDate, that.modifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdDate, modifiedDate);
    }
}
