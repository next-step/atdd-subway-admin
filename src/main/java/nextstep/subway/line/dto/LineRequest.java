package nextstep.subway.line.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.exception.LineNotFoundException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public boolean hasUpAndDownStation() {
        return this.upStationId != null && this.downStationId != null;
    }

    private void isEmpty(Long id) {
        if (id == null) {
            throw new LineNotFoundException("역 아이디를 찾을 수 없습니다.");
        }
    }
}
