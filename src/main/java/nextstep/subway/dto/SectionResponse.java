package nextstep.subway.dto;

import java.util.Objects;
import nextstep.subway.domain.line.LineStation;

public class SectionResponse {

    private Long id; // 구간 정보 아이디
    private Long upStationId; // 상행역 아이디
    private String upStationName; // 상행역 이름

    private Long downStationId; // 하행역 아이디
    private String downStationName; // 하행역 이름

    private int distance; // 구간 길이

    private Long lineId; // 노선 아이디
    private String lineName; // 노선 이름

    public static SectionResponse of(LineStation lineStation) {
        return new SectionResponse(lineStation.getId(),
                lineStation.getPreStationId(),
                lineStation.getPreStationName(),
                lineStation.getStationId(),
                lineStation.getStationName(),
                lineStation.getDistanceValue(),
                lineStation.getLineId(),
                lineStation.getLineName());
    }

    private SectionResponse(Long id,
            Long upStationId,
            String upStationName,
            Long downStationId,
            String downStationName,
            int distance,
            Long lineId,
            String lineName) {
        this.id = id;
        this.upStationId = upStationId;
        this.upStationName = upStationName;
        this.downStationId = downStationId;
        this.downStationName = downStationName;
        this.distance = distance;
        this.lineId = lineId;
        this.lineName = lineName;
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public String getUpStationName() {
        return upStationName;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public int getDistance() {
        return distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public String getLineName() {
        return lineName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionResponse that = (SectionResponse) o;
        return distance == that.distance && Objects.equals(id, that.id) && Objects.equals(upStationId,
                that.upStationId) && Objects.equals(upStationName, that.upStationName)
                && Objects.equals(downStationId, that.downStationId) && Objects.equals(downStationName,
                that.downStationName) && Objects.equals(lineId, that.lineId) && Objects.equals(lineName,
                that.lineName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStationId, upStationName, downStationId, downStationName, distance, lineId, lineName);
    }
}
