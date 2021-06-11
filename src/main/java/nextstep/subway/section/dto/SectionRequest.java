package nextstep.subway.section.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseDTO;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Getter @NoArgsConstructor
public class SectionRequest extends BaseDTO<Section> {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    @Builder
    public SectionRequest(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    @Override
    protected Section toEntity() {
        return new Section();
    }
}
