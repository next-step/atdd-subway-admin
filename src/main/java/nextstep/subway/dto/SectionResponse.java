package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class SectionResponse {
    private Long upStationId;
    private String upStationName;
    private Long downStationId;
    private String downStationName;
    private Long distance;

    public SectionResponse(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        this.upStationId = upStation.getId();
        this.upStationName = upStation.getName();
        this.downStationId = downStation.getId();
        this.downStationName = downStation.getName();
        this.distance = section.getDistance();
    }

    public SectionResponse(Long upStationId, String upStationName, Long downStationId, String downStationName, Long distance) {
        this.upStationId = upStationId;
        this.upStationName = upStationName;
        this.downStationId = downStationId;
        this.downStationName = downStationName;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public String getUpStationName() {
        return upStationName;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public Long getDistance() {
        return distance;
    }
}
