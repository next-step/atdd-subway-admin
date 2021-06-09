package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

import java.time.LocalDateTime;

public class SectionResponse {
    private String upStationName;
    private String downStationName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public SectionResponse(String upStationName, String downStationName, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.upStationName = upStationName;
        this.downStationName = downStationName;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(Section section){
        return new SectionResponse(section.getUpStation().getName(), section.getDownStation().getName(), section.getCreatedDate(), section.getModifiedDate());
    }

    public String getUpStationName() {
        return upStationName;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
