package nextstep.subway.dto.line.section;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;

public class CreateSectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private CreateSectionRequest() {

    }

    public CreateSectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Section toEntity(Line line, Station upStation, Station downStation) {
        return Section.of(line, upStation, downStation, distance);
    }
}
