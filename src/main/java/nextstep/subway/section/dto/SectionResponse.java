package nextstep.subway.section.dto;

import java.time.LocalDateTime;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionResponse {

    private Station upStation;
    private Station downStation;
    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public SectionResponse() {
    }

    public static SectionResponse of(final Section section) {
        return new SectionResponse(section);
    }

    private SectionResponse(final Section section) {
        upStation = section.getUpStation();
        downStation = section.getDownStation();
        distance = section.getDistance();
        createdDate = section.getCreatedDate();
        modifiedDate = section.getModifiedDate();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
