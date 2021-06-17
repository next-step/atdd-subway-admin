package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;
import java.util.Comparator;

public class StationResponse implements Comparable<StationResponse> {

    private static final Comparator<StationResponse> COMPARATOR = Comparator.comparingLong((station) -> station.getId());

    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(), station.getModifiedDate());
    }

    public StationResponse() {
    }

    public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
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
    public int compareTo(StationResponse o) {
        return COMPARATOR.compare(this, o);
    }
}
