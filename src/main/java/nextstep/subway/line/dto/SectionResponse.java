package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class SectionResponse {
    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    protected SectionResponse() {
    }

    public SectionResponse(Long id, StationResponse upStation, StationResponse downStation, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), StationResponse.of(section.getUpStation()), StationResponse.of(section.getDownStation()), section.getDistance());
    }

    public static List<SectionResponse> listOf(List<Section> stations) {
        return stations.stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
