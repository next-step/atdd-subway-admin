package nextstep.subway.section.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionStatus;
import nextstep.subway.station.domain.Station;

public class SectionRequest {
    private long lineId;
    private long stationId;
    private SectionStatus status;

    public SectionRequest() {
    }

    public SectionRequest(long lineId, long stationId, SectionStatus status) {
        this.lineId = lineId;
        this.stationId = stationId;
        this.status = status;
    }

    public long getLineId() {
        return lineId;
    }

    public long getStationId() {
        return stationId;
    }

    public SectionStatus getStatus() {
        return status;
    }

    public Section toSection(Station station, Line line) {
        return new Section(status, station, line);
    }
}
