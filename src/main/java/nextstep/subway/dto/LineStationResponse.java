package nextstep.subway.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStations;

public class LineStationResponse {
    private Long id;
    private long stationId;
    private long preStationId;
    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineStationResponse() {
    }

    public LineStationResponse(LineStation lineStation) {
        this.id = lineStation.getId();
        this.stationId = lineStation.getStationId();
        this.preStationId = lineStation.getPreStationId();
        this.distance = lineStation.getDistance();
        this.createdDate = lineStation.getCreatedDate();
        this.modifiedDate = lineStation.getModifiedDate();
    }

    public LineStation toLineStation() {
        return new LineStation(id, stationId, preStationId, distance);
    }

    public static List<LineStationResponse> to(LineStations lineStations) {
        List<LineStationResponse> lineStationResponses = new ArrayList<>();
        for (LineStation lineStation : lineStations.getLineStations()) {
            lineStationResponses.add(new LineStationResponse(lineStation));
        }
        return lineStationResponses;
    }

    public Long getId() {
        return id;
    }

    public long getStationId() {
        return stationId;
    }

    public long getPreStationId() {
        return preStationId;
    }

    public int getDistance() {
        return distance;
    }
}
