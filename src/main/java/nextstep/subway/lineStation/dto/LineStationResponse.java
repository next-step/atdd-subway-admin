package nextstep.subway.lineStation.dto;

import nextstep.subway.lineStation.domain.LineStation;

import java.time.LocalDateTime;

public class LineStationResponse {
    private Long id;
    private Long lineId;
    private String lineName;
    private Long stationId;
    private String stationName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineStationResponse() {
    }

    public static LineStationResponse of(LineStation lineStation) {
        return new LineStationResponse(lineStation.getId(), lineStation.getLine().getId(), lineStation.getLine().getName()
                , lineStation.getStation().getId(), lineStation.getStation().getName(), lineStation.getCreatedDate(), lineStation.getModifiedDate());
    }

    public LineStationResponse(Long id, Long lineId, String lineName, Long stationId, String stationName, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.lineId = lineId;
        this.lineName = lineName;
        this.stationId = stationId;
        this.stationName = stationName;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Long getId() {
        return this.id;
    }

    public Long getLineId() {
        return lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public Long getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
