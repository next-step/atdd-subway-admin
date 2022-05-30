package nextstep.subway.dto.response;

import nextstep.subway.dto.StationDTO;


public class SectionResponse {
    private Long id;
    private StationDTO upStationInfo;
    private StationDTO downStationInfo;
    private String lineName;
    private int distance;
    private Long nextSectionId;
    private Long backSectionId;

    public SectionResponse(Long id, StationDTO upStationDTO, StationDTO downStationDTO,
        String lineName, int distance, Long nextSectionId, Long backSectionId) {
        this.id = id;
        this.upStationInfo = upStationDTO;
        this.downStationInfo = downStationDTO;
        this.lineName = lineName;
        this.distance = distance;
        this.nextSectionId = nextSectionId;
        this.backSectionId = backSectionId;
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
