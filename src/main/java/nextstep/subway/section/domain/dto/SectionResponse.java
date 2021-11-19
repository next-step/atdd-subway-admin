package nextstep.subway.section.domain.dto;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

public class SectionResponse {

    private final Integer distance;
    private final StationResponse station;
    private final Integer sortSeq;

    public SectionResponse(Integer distance, StationResponse station, Integer sortSeq) {
        this.distance = distance;
        this.station = station;
        this.sortSeq = sortSeq;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getDistance(), StationResponse.of(section.getStation()),
            section.getSortSeq());
    }
}
