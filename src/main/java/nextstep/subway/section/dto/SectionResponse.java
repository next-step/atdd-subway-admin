package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class SectionResponse {
    private long id;
    private long lineId;
    private int distance;
    private List<StationResponse> stations;

    private SectionResponse() {
    }

    public SectionResponse(long id, long lineId, int distance, List<StationResponse> stations) {
        this.id = id;
        this.lineId = lineId;
        this.distance = distance;
        this.stations = stations;
    }

    public static SectionResponse of(Section section) {
        List<StationResponse> stations = section.getStations()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return new SectionResponse(
                section.getId(),
                section.getLineId(),
                section.getDistance(),
                stations);
    }

    public long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
