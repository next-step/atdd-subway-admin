package nextstep.subway.dto.response;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Section;
import nextstep.subway.dto.StationDTO;


public class SectionResponse {

    private Long id;
    private StationDTO upStationInfo;
    private StationDTO downStationInfo;
    private String lineName;
    private int distance;
    private Long nextSectionId;
    private Long backSectionId;

    protected SectionResponse(Long id, StationDTO upStationDTO, StationDTO downStationDTO,
        String lineName, Distance distance, Long nextSectionId, Long backSectionId) {
        this.id = id;
        this.upStationInfo = upStationDTO;
        this.downStationInfo = downStationDTO;
        this.lineName = lineName;
        this.distance = distance.getDistance();
        this.nextSectionId = nextSectionId;
        this.backSectionId = backSectionId;
    }

    public static SectionResponse from(Section section) {
        Long nextSectionId =
            section.getNextSection() != null ? section.getNextSection().getId() : -1;
        Long backSectionId =
            section.getBackSection() != null ? section.getBackSection().getId() : -1;

        return new SectionResponse(section.getId(), section.getUpStation().toStationDTO(),
            section.getDownStation().toStationDTO(), section.getLine().getName(),
            section.getDistance(), nextSectionId,
            backSectionId);
    }

    public Long getId() {
        return id;
    }

    public StationDTO getUpStationInfo() {
        return upStationInfo;
    }

    public StationDTO getDownStationInfo() {
        return downStationInfo;
    }

    public String getLineName() {
        return lineName;
    }

    public int getDistance() {
        return distance;
    }

    public Long getNextSectionId() {
        return nextSectionId;
    }

    public Long getBackSectionId() {
        return backSectionId;
    }
}
