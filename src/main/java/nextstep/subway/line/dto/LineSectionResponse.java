package nextstep.subway.line.dto;

import nextstep.subway.line.domain.LineSection;
import nextstep.subway.station.dto.StationResponse;

public class LineSectionResponse {
    private StationResponse station;
    private Long preStationId;
    private int distance;

    public LineSectionResponse() {
    }

    public LineSectionResponse(StationResponse station, Long preStationId, int distance) {
        this.station = station;
        this.preStationId = preStationId;
        this.distance = distance;
    }

    public static LineSectionResponse of(LineSection lineSection, StationResponse station) {
        return new LineSectionResponse(station, lineSection.getPreStationId(), lineSection.getDistance());
    }

    public StationResponse getStation() {
        return station;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public int getDistance() {
        return distance;
    }
}
