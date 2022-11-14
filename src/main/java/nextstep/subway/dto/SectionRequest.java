package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionRequest {

    private String downStationId;
    private String upStationId;
    private Integer distance;

    public Section toSection(Line line, Station upStation, Station downStation) {
        return new Section(line, upStation, downStation, distance);
    }

    public Long getDownStationId() {
        return Long.parseLong(downStationId);
    }

    public Long getUpStationId() {
        return Long.parseLong(upStationId);
    }

    public Integer getDistance() {
        return distance;
    }
}
