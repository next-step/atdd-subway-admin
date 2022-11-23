package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {
    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(),
                section.getUpStation() == null ? null : StationResponse.of(section.getUpStation()),
                section.getDownStation() == null ? null : StationResponse.of(section.getDownStation()),
                section.getDistance());
    }

    public SectionResponse(Long id, StationResponse upStation, StationResponse downStation, int distance) {
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

    public int getDistance() {
        return distance;
    }
}
