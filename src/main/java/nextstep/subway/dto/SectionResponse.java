package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {
    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private DistanceResponse distance;

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(),
                section.getUpStation() == null ? null : StationResponse.of(section.getUpStation()),
                section.getDownStation() == null ? null : StationResponse.of(section.getDownStation()),
                DistanceResponse.of(section.getDistance()));
    }

    public SectionResponse(Long id, StationResponse upStation, StationResponse downStation, DistanceResponse distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public DistanceResponse getDistance() {
        return distance;
    }
}
