package nextstep.subway.line.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseDTO;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @NoArgsConstructor
public class LineRequest extends BaseDTO<Line> {

    @NotBlank
    private String name;

    @NotBlank
    private String color;

    @NotNull
    private Long upStationId;

    @NotNull
    private Long downStationId;

    @Min(value = 0)
    private int distance;

    @Builder
    private LineRequest(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    @Override
    protected Line toEntity() {
        return new Line(name, color);
    }

    public Line toLine() {
        return toEntity();
    }

    public SectionRequest toSectionRequest() {
        return SectionRequest.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
    }
}
