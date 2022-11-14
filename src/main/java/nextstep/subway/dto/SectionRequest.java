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

    public String getDownStationId() {
        return downStationId;
    }

    public String getUpStationId() {
        return upStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
