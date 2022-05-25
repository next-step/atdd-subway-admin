package nextstep.subway.dto;

import nextstep.subway.domain.Station;

import java.time.LocalDateTime;

public class StationResponseDTO {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static StationResponseDTO of(Station station) {
        return new StationResponseDTO(station.getId(), station.getName(), station.getCreatedDate(), station.getModifiedDate());
    }

    public StationResponseDTO() {
    }

    public StationResponseDTO(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
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
}
