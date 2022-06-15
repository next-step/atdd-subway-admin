package nextstep.subway.dto;

import nextstep.subway.domain.Section;

import java.util.Arrays;
import java.util.List;

public class SectionResponse {
    private Long id;
    private Integer distance;
    private List<StationResponse> upDownStations;

    private SectionResponse(Long id, Integer distance, List<StationResponse> upDownStations) {
        this.id = id;
        this.distance = distance;
        this.upDownStations = upDownStations;
    }

    private SectionResponse(){
    }

    public static SectionResponse of(Section section) {
        List<StationResponse> upDownStations = Arrays.asList(
                StationResponse.of(section.getUpStation()),
                StationResponse.of(section.getDownStation())
        );
        return new SectionResponse(section.getId(), section.getDistance(), upDownStations);
    }

    public Long getId() {
        return id;
    }

    public Integer getDistance() {
        return distance;
    }

    public List<StationResponse> getUpDownStations() {
        return upDownStations;
    }
}
