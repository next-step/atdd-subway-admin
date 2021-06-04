package nextstep.subway.section.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;

import java.util.Objects;

public class SectionDto {
    private Line line;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionDto(Line line, Long upStationId, Long downStationId, int distance) {
        this.line = line;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionDto(Line persistLine, LineRequest request) {
        this.line = persistLine;
        this.upStationId = request.getUpStationId();
        this.downStationId = request.getDownStationId();
        this.distance = request.getDistance();
    }

    public boolean checkStationIdExist() {
        return Objects.nonNull(upStationId) && Objects.nonNull(downStationId);
    }

    public Line getLine() {
        return line;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
