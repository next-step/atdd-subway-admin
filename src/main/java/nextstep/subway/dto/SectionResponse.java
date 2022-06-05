package nextstep.subway.dto;

import nextstep.subway.domain.Section;

import java.util.Arrays;
import java.util.List;

public class SectionResponse {
    private final Long id;
    private final Integer distance;
    private final List<StationResponse> stations;

    private SectionResponse(Long id, Integer distance, List<StationResponse> stations) {
        this.id = id;
        this.distance = distance;
        this.stations = stations;
    }

    public static SectionResponse of(Section section) {
        List<StationResponse> stations = Arrays.asList(
                StationResponse.of(section.getUpStation()),
                StationResponse.of(section.getDownStation())
        );
        return new SectionResponse(section.getId(), section.getDistance(), stations);
    }

    public Long getId() {
        return id;
    }

    public Integer getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
