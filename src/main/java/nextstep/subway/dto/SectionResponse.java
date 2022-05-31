package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {
    private String startStationName;
    private Long distanceToNextStation;

    public SectionResponse() {
    }

    public SectionResponse(String startStationName, Long distanceToNextStation) {
        this.startStationName = startStationName;
        this.distanceToNextStation = distanceToNextStation;
    }

    public static SectionResponse of(Section section){
        return new SectionResponse(section.getUpStation().getName(), section.getDistance());
    }

    public String getStartStationName() {
        return startStationName;
    }

    public Long getDistanceToNextStation() {
        return distanceToNextStation;
    }
}
