package nextstep.subway.station.dto;

import java.time.LocalDateTime;

public class StationDto {

    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private StationDto(Long id,
                       String name,
                       LocalDateTime createdDate,
                       LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static StationDto of(Long id,
                                String name,
                                LocalDateTime createdDate,
                                LocalDateTime modifiedDate) {
        return new StationDto(id, name, createdDate, modifiedDate);
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
